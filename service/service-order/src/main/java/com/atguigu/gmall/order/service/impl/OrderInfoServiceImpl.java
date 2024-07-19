package com.atguigu.gmall.order.service.impl;
import com.atguigu.gmall.ware.entity.Sku;
import com.atguigu.gmall.ware.entity.WareSkuMapVo;
import com.atguigu.gmall.ware.entity.WareStockResultMsg;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.entity.UserAuthInfoVo;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.UserAuthUtils;
import com.atguigu.gmall.enums.OrderStatus;
import com.atguigu.gmall.enums.ProcessStatus;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.SkuDetailFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.order.dto.DetailDTO;
import com.atguigu.gmall.order.dto.OrderSubmitDTO;
import com.atguigu.gmall.order.entity.OrderDetail;
import com.atguigu.gmall.order.entity.OrderStatusLog;
import com.atguigu.gmall.order.entity.PaymentInfo;
import com.atguigu.gmall.order.mapper.OrderStatusLogMapper;
import com.atguigu.gmall.order.mapper.PaymentInfoMapper;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.rabbit.constant.MqConstant;
import com.atguigu.gmall.ware.entity.WareStockMsg;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.infra.executor.sql.prepare.driver.vertx.builder.PreparedQueryExecutionUnitBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【order_info(订单表 订单表)】的数据库操作Service实现
* @createDate 2023-02-25 14:20:14
*/
@Slf4j
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Autowired
    private SkuDetailFeignClient skuDetailFeignClient ;

    @Autowired
    private OrderInfoMapper orderInfoMapper ;

    @Autowired
    private OrderStatusLogMapper orderStatusLogMapper ;

    @Autowired
    private CartFeignClient cartFeignClient ;

    @Autowired
    private WareFeignClient wareFeignClient ;

    @Autowired
    private RabbitTemplate rabbitTemplate ;

    @Autowired
    private PaymentInfoMapper paymentInfoMapper ;

    @Autowired
    private OrderDetailService orderDetailService ;

    @Override
    public String submitOrder(OrderSubmitDTO orderSubmitDTO, String tradeNo) {

        log.info("OrderInfoServiceImpl.....submitOrder方法执行了...");

        // 判断redis中是否存在tradeNo，如果存在就删除。需要使用lua
        String script = "if redis.call(\"EXISTS\",KEYS[1])\n" +
                "then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end" ;
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script , Long.class), Arrays.asList(GmallConstant.REDIS_ORDER_CONFIRM_PREFIX + tradeNo));
        if(result == 0) {    // 表达重复提交了
            throw new GmallException(ResultCodeEnum.REQ_REPEAT) ;
        }

        // 价格校验
        List<DetailDTO> orderDetailList = orderSubmitDTO.getOrderDetailList();
        List<DetailDTO> detailDTOList = orderDetailList.stream().filter(detailDTO -> {
            Long skuId = detailDTO.getSkuId();          // 获取skuId
            Result<SkuInfo> skuInfoResult = skuDetailFeignClient.findSkuInfoBySkuId(skuId);
            SkuInfo skuInfo = skuInfoResult.getData();
            log.info("skuId: {} , skuInfoPrice: {} , detailDTOPrice: {}" , skuInfo.getId() , skuInfo.getPrice() , detailDTO.getOrderPrice());
            return skuInfo.getPrice().intValue() != detailDTO.getOrderPrice().intValue();  // 不相等保留起来
        }).collect(Collectors.toList());

        if(detailDTOList != null && detailDTOList.size() > 0) {     // 说明前端提价过来的价格和数据库中保存的价格不一致
            throw new GmallException(ResultCodeEnum.SKU_PRICE_CHANGE) ;
        }

        // 校验库存数据
        List<DetailDTO> detailDTOS = orderDetailList.stream().filter(detailDTO -> {

            // 获取skuId和skuNum
            Long skuId = detailDTO.getSkuId();
            Integer skuNum = detailDTO.getSkuNum();

            // 调用库存系统微服务判断该商品是否存在库存
            String hasStock = wareFeignClient.hasStock(skuId, skuNum);

            // 判断
            return !"1".equals(hasStock) ;  // 保留没有库存的商品数据到流中

        }).collect(Collectors.toList());
        if(detailDTOS != null && detailDTOS.size() > 0) {
            throw new GmallException(ResultCodeEnum.SKU_NOT_HAS_STOCK) ;
        }

        // 保存订单数据
        OrderInfo orderInfo = saveOrderInfo(orderSubmitDTO, tradeNo);

        // 保存订单明细数据: 就是购物项明细数据转换成订单明细数据然后进行保存
        saveOrderDetail(orderSubmitDTO , orderInfo) ;

        // 保存日志数据
        saveOrderStatusLog(orderInfo) ;

        // 删除购物车相关数据
        cartFeignClient.deleteCheckedCart() ;

        // 向MQ中发送延迟消息
        /**
         * 思考：需要向mq中发送哪些消息?
         * orderId
         * useId
         */
        Map<String , Long> orderTimeOutMap = new HashMap<>() ;
        orderTimeOutMap.put("userId" , orderInfo.getUserId()) ;
        orderTimeOutMap.put("orderId" , orderInfo.getId()) ;

        // 发送消息
        rabbitTemplate.convertAndSend(MqConstant.ORDER_TIMEOUT_EXCHANGE , MqConstant.ORDER_ROUTINGKEY , JSON.toJSONString(orderTimeOutMap));

        return String.valueOf(orderInfo.getId());
    }

    /**
     * 订单的关闭操作
     * @param userId
     * @param orderId
     */
    @Override
    public void closeOrder(Long userId, Long orderId) {

        // 根据订单的id和用户的id查询数据库
        LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        lambdaQueryWrapper.eq(OrderInfo::getId , orderId) ;
        lambdaQueryWrapper.eq(OrderInfo::getUserId , userId) ;          // 使用分片键进行查询
        OrderInfo orderInfo = orderInfoMapper.selectOne(lambdaQueryWrapper);

        // 判断订单的状态是否为未支付
        if(orderInfo.getOrderStatus().equals(OrderStatus.UNPAID.name()) && orderInfo.getProcessStatus().equals(ProcessStatus.UNPAID.name())) {

            // 进行关单操作，就是把订单的状态更改close
            orderInfo.setOrderStatus(OrderStatus.CLOSED.name());
            orderInfo.setProcessStatus(ProcessStatus.CLOSED.name());  // 思考问题：直接进行修改安全不？不安全

            // 使用乐观锁进行订单状态的修改
            lambdaQueryWrapper.eq(OrderInfo::getOrderStatus , OrderStatus.UNPAID.name()) ;
            lambdaQueryWrapper.eq(OrderInfo::getProcessStatus , ProcessStatus.UNPAID.name()) ;
            orderInfoMapper.update(orderInfo , lambdaQueryWrapper) ;

        }

    }

    /**
     * 进行订单状态的修改
     * @param messageBody
     */
    @Override
    public void orderPayedStatus(String messageBody) {

        // 1、保存支付信息到PaymetInfo表
        PaymentInfo paymentInfo = getPaymentInfo(messageBody) ;
        paymentInfoMapper.insert(paymentInfo) ;

        // 2、修改订单的状态
        upateOrderStatus(paymentInfo) ;

        // 3、发送扣减库存的消息
        WareStockMsg wareStockMsg  = getWareStockMsg(messageBody , paymentInfo) ;
        rabbitTemplate.convertAndSend(MqConstant.WARE_STOCK_EXCHANGE_NAME , MqConstant.WARE_STOCK_ROUTING_KEY , JSON.toJSONString(wareStockMsg));

    }

    /**
     * 根据库存扣减的结果修改订单状态
     * 订单的状态是已支付此处才进行订单状态的修改
     * @param msg
     */
    @Override
    public void updateOrderStatusWare(String msg) {

        // 把msg字符串转换成对象 WareStockResultMsg
        WareStockResultMsg wareStockResultMsg = JSON.parseObject(msg , WareStockResultMsg.class) ;

        // 查询订单的数据
        Long orderId = wareStockResultMsg.getOrderId();
        LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        lambdaQueryWrapper.eq(OrderInfo::getId , orderId) ;
        OrderInfo orderInfo = orderInfoMapper.selectOne(lambdaQueryWrapper);

        // 判断订单的状态是否为已支付
        if(OrderStatus.PAID.name().equals(orderInfo.getOrderStatus()) && ProcessStatus.PAID.name().equals(orderInfo.getProcessStatus())) {

            // 给orderInfo设置新的订单状态
            OrderStatus orderStatus = null ;
            ProcessStatus processStatus = null ;

            // 获取库存系统扣减库存完毕以后的消息
            String status = wareStockResultMsg.getStatus();
            switch (status) {
                case "DEDUCTED" :
                    orderStatus =  OrderStatus.WAITING_DELEVER ;
                    processStatus = ProcessStatus.NOTIFIED_WARE ;
                    break;
                case "OUT_OF_STOCK" :
                    orderStatus = OrderStatus.WAITING_SCHEDULE ;
                    processStatus = ProcessStatus.STOCK_EXCEPTION ;
                    break;
            }

            // 把确定的状态值设置给orderInfo对象
            orderInfo.setOrderStatus(orderStatus.name());
            orderInfo.setProcessStatus(processStatus.name());

            // 更新数据库，使用cas
            lambdaQueryWrapper.eq(OrderInfo::getOrderStatus , OrderStatus.PAID.name()) ;
            lambdaQueryWrapper.eq(OrderInfo::getProcessStatus , ProcessStatus.PAID.name()) ;
            lambdaQueryWrapper.eq(OrderInfo::getUserId , orderInfo.getUserId()) ;

            // 执行更新操
            orderInfoMapper.update(orderInfo , lambdaQueryWrapper) ;

        }

    }

    /**
     * 进行拆带操作
     * @param orderId
     * @param wareSkuMap：
     * @return
     */
    @Override
    public List<WareStockMsg> orderSplit(Long orderId, String wareSkuMap) {

        // 一个仓库就是一个订单
        //  [{"wareId":"1","skuIds":["2","10"]},{"wareId":"2","skuIds":["3"]}]
        List<WareSkuMapVo> skuMapVoList = JSON.parseArray(wareSkuMap, WareSkuMapVo.class);

        // 查询老订单的数据
        LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        lambdaQueryWrapper.eq(OrderInfo::getId , orderId) ;
        OrderInfo oldOrderInfo = orderInfoMapper.selectOne(lambdaQueryWrapper);

        // 定义要返回的数据的集合
        List<WareStockMsg> wareStockMsgList = new ArrayList<>() ;

        // 对skuMapVoList进行遍历
        for(WareSkuMapVo wareSkuMapVo : skuMapVoList) {

            // 构建新的订单
            OrderInfo childOrderInfo = new OrderInfo() ;
            childOrderInfo.setConsignee(oldOrderInfo.getConsignee());
            childOrderInfo.setConsigneeTel(oldOrderInfo.getConsigneeTel());
            childOrderInfo.setOrderStatus(oldOrderInfo.getOrderStatus());
            childOrderInfo.setUserId(oldOrderInfo.getUserId());
            childOrderInfo.setPaymentWay(oldOrderInfo.getPaymentWay());
            childOrderInfo.setDeliveryAddress(oldOrderInfo.getDeliveryAddress());
            childOrderInfo.setOrderComment(oldOrderInfo.getOrderComment());
            childOrderInfo.setOutTradeNo(oldOrderInfo.getOutTradeNo());

            // 查询订单明细数据
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>() ;
            orderDetailLambdaQueryWrapper.in(OrderDetail::getSkuId , wareSkuMapVo.getSkuIds()) ;
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId , orderId) ;
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getUserId , oldOrderInfo.getUserId()) ;
            List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailLambdaQueryWrapper);

            // 新的订单的总金额
            BigDecimal totalAmount = orderDetailList.stream().map(orderDetail -> {
                return new BigDecimal(orderDetail.getOrderPrice()).multiply(new BigDecimal(orderDetail.getSkuNum()));
            }).reduce((o1, o2) -> o1.add(o2)).get();
            childOrderInfo.setTotalAmount(totalAmount);
            childOrderInfo.setTradeBody(orderDetailList.get(0).getSkuName());
            childOrderInfo.setCreateTime(new Date());
            childOrderInfo.setExpireTime(oldOrderInfo.getExpireTime());
            childOrderInfo.setProcessStatus(oldOrderInfo.getProcessStatus());
            childOrderInfo.setTrackingNo("");       // 物流单号
            childOrderInfo.setParentOrderId(oldOrderInfo.getId());
            childOrderInfo.setImgUrl(orderDetailList.get(0).getImgUrl());

            childOrderInfo.setProvinceId(0L);
            childOrderInfo.setOperateTime(new Date());
            childOrderInfo.setActivityReduceAmount(new BigDecimal("0"));
            childOrderInfo.setCouponAmount(new BigDecimal("0"));
            childOrderInfo.setOriginalTotalAmount(new BigDecimal("0"));
            childOrderInfo.setFeightFee(new BigDecimal("0"));
            childOrderInfo.setRefundableTime(new Date());

            // 保存新的订单
            orderInfoMapper.insert(childOrderInfo);

            // 构建新的订单的订单明细
            List<OrderDetail> detailList = orderDetailList.stream().map(orderDetail -> {
                OrderDetail newOrderDetail = new OrderDetail();
                BeanUtils.copyProperties(orderDetail, newOrderDetail);
                newOrderDetail.setOrderId(childOrderInfo.getId());
                newOrderDetail.setId(null);
                return newOrderDetail;
            }).collect(Collectors.toList());

            // 存储新的订单明细数据
            orderDetailService.saveBatch(detailList) ;

            // 构建WareStockMsg对象
            WareStockMsg wareStockMsg = new WareStockMsg() ;
            wareStockMsg.setOrderId(childOrderInfo.getId());
            wareStockMsg.setConsignee(childOrderInfo.getConsignee());
            wareStockMsg.setConsigneeTel(childOrderInfo.getConsigneeTel());
            wareStockMsg.setOrderComment(childOrderInfo.getOrderComment());
            wareStockMsg.setOrderBody(childOrderInfo.getTradeBody());
            wareStockMsg.setDeliveryAddress(childOrderInfo.getDeliveryAddress());
            wareStockMsg.setPaymentWay("2");
            wareStockMsg.setWareId(wareSkuMapVo.getWareId());
            List<Sku> skuList = detailList.stream().map(detail -> {
                Sku sku = new Sku();
                sku.setSkuId(detail.getSkuId());
                sku.setSkuName(detail.getSkuName());
                sku.setSkuNum(Integer.parseInt(detail.getSkuNum()));
                return sku;
            }).collect(Collectors.toList());
            wareStockMsg.setDetails(skuList);

            // 把WareStockMsg对象存储到集合中
            wareStockMsgList.add(wareStockMsg) ;

        }

        // 把之前订单的状态改为已拆单
        if(OrderStatus.PAID.name().equals(oldOrderInfo.getOrderStatus()) && ProcessStatus.PAID.name().equals(oldOrderInfo.getProcessStatus())) {

            oldOrderInfo.setOrderStatus(OrderStatus.SPLIT.name());
            oldOrderInfo.setProcessStatus(ProcessStatus.SPLIT.name());

            // 使用乐观锁进行修改
            LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(OrderInfo::getId , oldOrderInfo.getId()) ;
            lambdaQueryWrapper1.eq(OrderInfo::getUserId , oldOrderInfo.getUserId()) ;
            lambdaQueryWrapper1.eq(OrderInfo::getOrderStatus , OrderStatus.PAID.name()) ;
            lambdaQueryWrapper1.eq(OrderInfo::getProcessStatus , ProcessStatus.PAID.name()) ;
            orderInfoMapper.update(oldOrderInfo , lambdaQueryWrapper1) ;

        }

        return wareStockMsgList;
    }

    @Override
    public Page findOrderListByUserId(Integer pageNo, Integer pageSize) {

        //获取当前登录用户
        UserAuthInfoVo userAuthInfo = UserAuthUtils.getUserAuthInfo();
        Long userId = userAuthInfo.getUserId();

        // 进行查询
        Page<OrderInfo> page = new Page(pageNo , pageSize) ;
        LambdaQueryWrapper<OrderInfo> orderInfoLambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        orderInfoLambdaQueryWrapper.eq(OrderInfo::getUserId , userId) ;
        orderInfoLambdaQueryWrapper.ne(OrderInfo::getOrderStatus , OrderStatus.SPLIT.name()) ;
        page(page , orderInfoLambdaQueryWrapper) ;

        // 当前页的数据，然后遍历这个集合获取每一个订单数据，然后查询订单明细，查询完毕以后把订单明细数据在此设置给订单
        page.getRecords().stream().forEach(oderInfo -> {

            // 查询订单明细
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
            lambdaQueryWrapper.eq(OrderDetail::getOrderId , oderInfo.getId()) ;
            lambdaQueryWrapper.eq(OrderDetail::getUserId , oderInfo.getUserId()) ;
            List<OrderDetail> orderDetailList = orderDetailService.list(lambdaQueryWrapper);

            // 查询完毕以后把订单明细数据在此设置给订单
            oderInfo.setOrderDetailList(orderDetailList);

        });

        return page;
    }

    /**
     * 构建扣减库存的消息
     * @param messageBody
     * @param paymentInfo
     * @return
     */
    private WareStockMsg getWareStockMsg(String messageBody, PaymentInfo paymentInfo) {

        // 创建WareStockMsg对象
        WareStockMsg wareStockMsg = new WareStockMsg() ;

        // 根据订单的id和订单的用户id查询数据库
        LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        lambdaQueryWrapper.eq(OrderInfo::getId , paymentInfo.getOrderId()) ;
        lambdaQueryWrapper.eq(OrderInfo::getUserId , paymentInfo.getUserId()) ;
        OrderInfo orderInfo = orderInfoMapper.selectOne(lambdaQueryWrapper);

        // 给WareStockMsg属性赋值
        wareStockMsg.setOrderId(Long.parseLong(paymentInfo.getOrderId()));
        wareStockMsg.setConsignee(orderInfo.getConsignee());
        wareStockMsg.setConsigneeTel(orderInfo.getConsigneeTel());
        wareStockMsg.setOrderComment(orderInfo.getOrderComment());
        wareStockMsg.setOrderBody(orderInfo.getTradeBody());
        wareStockMsg.setDeliveryAddress(orderInfo.getDeliveryAddress());
        wareStockMsg.setPaymentWay("2");

        // 给WareStockMsg设置明细数据
        // 根据订单的id查询订单明细
        LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId , orderInfo.getId()) ;
        orderDetailLambdaQueryWrapper.eq(OrderDetail::getUserId , orderInfo.getUserId()) ;
        List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailLambdaQueryWrapper);
        List<Sku> details = orderDetailList.stream().map(orderDetail -> {
            Sku sku = new Sku();
            sku.setSkuId(orderDetail.getSkuId());
            sku.setSkuName(orderDetail.getSkuName());
            sku.setSkuNum(Integer.parseInt(orderDetail.getSkuNum()));
            return sku;
        }).collect(Collectors.toList());
        wareStockMsg.setDetails(details);

        // 返回
        return wareStockMsg ;

    }

    /**
     *
     * 未支付  ----> 已支付
     *
     * close.queue：关闭订单队列
     * order.payed.queue: 更改订单的支付状态的队列
     *
     * 如果此时是order.payed.queue中的消息先执行了，我们把订单的状态改为已支付。close.queue队列中的消息后执行了，那么此时不会将订单的状态改为已关闭！
     * 如果关闭订单的队列先执行了，那么此时订单的状态就是已关闭，而order.payed.queue队列中的消息后执行了，那么此时应该怎么办？ 把订单的状态改为已支付
     *
     * 我们在进行订单状态修改的时候，需要判断当前订单的状态：如果是未支付或者已关闭
     *
     * 修改订单的状态
     * @param paymentInfo
     */
    private void upateOrderStatus(PaymentInfo paymentInfo) {

        // 查询订单数据
        LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        lambdaQueryWrapper.eq(OrderInfo::getId , paymentInfo.getOrderId()) ;
        lambdaQueryWrapper.eq(OrderInfo::getUserId , paymentInfo.getUserId()) ;
        OrderInfo orderInfo = orderInfoMapper.selectOne(lambdaQueryWrapper);

        // 判断订单的状态
        if(( OrderStatus.UNPAID.name().equals(orderInfo.getOrderStatus()) || OrderStatus.CLOSED.name().equals(orderInfo.getOrderStatus()) ) &&
                (ProcessStatus.UNPAID.name().equals(orderInfo.getProcessStatus()) || ProcessStatus.CLOSED.name().equals(orderInfo.getProcessStatus()) )
        ) {

            /**
             * 到底应该把订单的状态改成什么是取决于支付宝返回返回的交易状态！
             */
            // 更改订单的状态为已支付 , CAS算法进行更改
            orderInfo.setOrderStatus(OrderStatus.PAID.name());
            orderInfo.setProcessStatus(ProcessStatus.PAID.name());

            // 给lambdaQueryWrapper对象设置更改时所对应条件
            lambdaQueryWrapper.in(OrderInfo::getOrderStatus , OrderStatus.UNPAID.name() , OrderStatus.CLOSED.name()) ;
            lambdaQueryWrapper.in(OrderInfo::getProcessStatus , ProcessStatus.UNPAID.name() , ProcessStatus.CLOSED.name()) ;

            orderInfoMapper.update(orderInfo , lambdaQueryWrapper) ;

        }

    }

    /**
     * 构建PaymentInfo对象
     * @param messageBody
     * @return
     */
    private PaymentInfo getPaymentInfo(String messageBody) {

        // 把messageBody转换成Map
        Map<String , Object> map = JSON.parseObject(messageBody, Map.class);

        // 获取外部交易号
        String outTradeNo = map.get("out_trade_no").toString();

        // 查询订单数据
        LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        lambdaQueryWrapper.eq(OrderInfo::getOutTradeNo , outTradeNo) ;
        OrderInfo orderInfo = orderInfoMapper.selectOne(lambdaQueryWrapper);

        // 创建一个PaymentInfo对象
        PaymentInfo paymentInfo = new PaymentInfo() ;

        // 给PaymentInfo的属性设置值
        paymentInfo.setUserId(orderInfo.getUserId());
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setOrderId(String.valueOf(orderInfo.getId()));
        paymentInfo.setPaymentType("ALIPAY");
        paymentInfo.setTradeNo(map.get("trade_no").toString());
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
        paymentInfo.setSubject(orderInfo.getTradeBody());
        paymentInfo.setPaymentStatus(map.get("trade_status").toString());
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(messageBody);

        // 返回
        return paymentInfo ;

    }

    // 订单订单操作日志数据
    private void saveOrderStatusLog(OrderInfo orderInfo) {
        OrderStatusLog orderStatusLog = new OrderStatusLog() ;
        orderStatusLog.setUserId(orderInfo.getUserId());
        orderStatusLog.setOrderId(orderInfo.getId());
        orderStatusLog.setOrderStatus(orderInfo.getOrderStatus());
        orderStatusLog.setOperateTime(new Date());
        orderStatusLogMapper.insert(orderStatusLog) ;
    }

    // 保存订单明细数据
    private void saveOrderDetail(OrderSubmitDTO orderSubmitDTO , OrderInfo orderInfo ) {

        List<DetailDTO> detailDTOS = orderSubmitDTO.getOrderDetailList();
        List<OrderDetail> detailList = detailDTOS.stream().map(detailDTOVo -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setUserId(orderInfo.getUserId());
            orderDetail.setOrderId(orderInfo.getId());
            orderDetail.setSkuId(detailDTOVo.getSkuId());
            orderDetail.setSkuName(detailDTOVo.getSkuName());
            orderDetail.setImgUrl(detailDTOVo.getImgUrl());
            orderDetail.setOrderPrice(detailDTOVo.getOrderPrice().intValue());
            orderDetail.setSkuNum(String.valueOf(detailDTOVo.getSkuNum()));
            orderDetail.setCreateTime(new Date());
            orderDetail.setSplitTotalAmount(detailDTOVo.getOrderPrice().multiply(new BigDecimal(detailDTOVo.getSkuNum())));      // 订单明细的金额
            orderDetail.setSplitActivityAmount(new BigDecimal("0"));   // 参加的活动金额，促销金额
            orderDetail.setSplitCouponAmount(new BigDecimal("0"));     // 优惠券的金额
            return orderDetail;
        }).collect(Collectors.toList());
        orderDetailService.saveBatch(detailList) ;      // 保存订单明细数据

    }

    // 保存订单数据的方法
    private OrderInfo saveOrderInfo(OrderSubmitDTO orderSubmitDTO , String tradeNo) {

        OrderInfo orderInfo = new OrderInfo() ;
        orderInfo.setConsignee(orderSubmitDTO.getConsignee());
        orderInfo.setConsigneeTel(orderSubmitDTO.getConsigneeTel());
        List<DetailDTO> dtoOrderDetailList = orderSubmitDTO.getOrderDetailList();
        BigDecimal totalAmount = dtoOrderDetailList.stream().map(detailDTO -> {
            return detailDTO.getOrderPrice().multiply(new BigDecimal(detailDTO.getSkuNum()));
        }).reduce((o1, o2) -> o1.add(o2)).get();
        orderInfo.setTotalAmount(totalAmount);
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());        // 订单的状态，粗粒度的订单状态

        UserAuthInfoVo userAuthInfo = UserAuthUtils.getUserAuthInfo();
        orderInfo.setUserId(userAuthInfo.getUserId());
        orderInfo.setPaymentWay(orderSubmitDTO.getPaymentWay());
        orderInfo.setDeliveryAddress(orderSubmitDTO.getDeliveryAddress());
        orderInfo.setOrderComment(orderSubmitDTO.getOrderComment());
        orderInfo.setOutTradeNo(tradeNo);
        DetailDTO detailDTO = dtoOrderDetailList.get(0);
        orderInfo.setTradeBody(detailDTO.getSkuName());         // 订单体数据，就是skuName
        orderInfo.setCreateTime(new Date());                    // 订单的创建时间

        long timteOutDate = System.currentTimeMillis() + 1000 * 30 * 60;
        orderInfo.setExpireTime(new Date(timteOutDate));                    // 订单的过期时间

        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());           // 订单处理状态，细粒度的订单状态
        orderInfo.setTrackingNo("");            //  物流单编号
        orderInfo.setImgUrl(detailDTO.getImgUrl());
        orderInfo.setOperateTime(new Date());
        orderInfo.setActivityReduceAmount(new BigDecimal("0"));
        orderInfo.setCouponAmount(new BigDecimal("0"));
        orderInfo.setOriginalTotalAmount(new BigDecimal("0"));
        orderInfo.setFeightFee(new BigDecimal("0"));
        orderInfo.setRefundableTime(new Date());
        orderInfoMapper.insert(orderInfo) ;

        return orderInfo ;
    }


}




