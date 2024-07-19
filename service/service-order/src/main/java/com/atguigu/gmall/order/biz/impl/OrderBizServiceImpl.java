package com.atguigu.gmall.order.biz.impl;
import java.math.BigDecimal;

import com.atguigu.gmall.cart.entity.UserAuthInfoVo;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.common.util.UserAuthUtils;
import com.atguigu.gmall.feign.product.SkuDetailFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.vo.DetailVo;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;

import com.atguigu.gmall.cart.entity.CartItem;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.user.UserAddressFeignClient;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import com.atguigu.gmall.user.entity.UserAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderBizServiceImpl implements OrderBizService {

    @Autowired
    private CartFeignClient cartFeignClient ;

    @Autowired
    private UserAddressFeignClient userAddressFeignClient ;

    @Autowired
    private SkuDetailFeignClient skuDetailFeignClient ;

    @Autowired
    private OrderInfoMapper orderInfoMapper ;

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Autowired
    private WareFeignClient wareFeignClient ;

    @Override
    public OrderConfirmVo orderConfirmData() {
        log.info("OrderBizServiceImpl....orderConfirmData方法执行了...");

        // 远程调用购物车模块的接口 查询用户选中的购物车数据
        Result<List<CartItem>> checkedCartItem = cartFeignClient.getCheckedCartItem();

        // 远程调用用户微服务的接口，获取用户的收货地址数据
        Result<List<UserAddress>> userAddressListResult = userAddressFeignClient.findByUserId();

        // 把远程调用完毕以后得到的结果数据封装到OrderConfirmVo对象中
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo() ;          // 创建一个OrderConfirmVo对象

        // 购物项的明细数据
        List<CartItem> cartItemList = checkedCartItem.getData();
        List<DetailVo> detailVoList = cartItemList.stream().map(cartItem -> {

            // 创建DetailVo对象，封装前端所需要的购物项明细数据
            DetailVo detailVo = new DetailVo();
            detailVo.setImgUrl(cartItem.getImgUrl());
            detailVo.setSkuName(cartItem.getSkuName());

            // 获取最新的价格
            Result<SkuInfo> skuInfoResult = skuDetailFeignClient.findSkuInfoBySkuId(cartItem.getSkuId());
            SkuInfo skuInfo = skuInfoResult.getData();
            detailVo.setOrderPrice(new BigDecimal(skuInfo.getPrice()));

            // 调用库存系统查看是否存在库存
            String hasStock = wareFeignClient.hasStock(cartItem.getSkuId(), cartItem.getSkuNum());
            detailVo.setHasStock(Integer.parseInt(hasStock));
            log.info("调用库存系统的接口，判断当前这个商品是否存在库存, skuId:{} , hasStock: {}"  , cartItem.getSkuId() , hasStock);

            detailVo.setSkuNum(cartItem.getSkuNum());
            detailVo.setSkuId(cartItem.getSkuId());

            return detailVo;

        }).collect(Collectors.toList());
        orderConfirmVo.setDetailArrayList(detailVoList);


        // 购物项购买的商品数量的总和
        Integer totalNum = detailVoList.stream().map(detailVo -> detailVo.getSkuNum())
                .reduce((o1, o2) -> o1 + o2).get();
        orderConfirmVo.setTotalNum(totalNum);

        // 购物项购买的商品价格的总和
        BigDecimal totalAmount = detailVoList.stream().map(detailVo -> {

            // 计算每一个购物项的总价格= 每一个购物项的商品的价格 * 购买数量
            return detailVo.getOrderPrice().multiply(new BigDecimal(detailVo.getSkuNum()));

        }).reduce((o1, o2) -> o1.add(o2)).get();
        orderConfirmVo.setTotalAmount(totalAmount);

        // 封装收货人地址数据
        orderConfirmVo.setUserAddressList(userAddressListResult.getData());

        // 外部交易号
        String tradeNo = UUID.randomUUID().toString().replace("-", "");
        orderConfirmVo.setTradeNo(tradeNo);

        // 把这个外部交易号存储到Redis中，目的防止表达式重复提交
        redisTemplate.opsForValue().set(GmallConstant.REDIS_ORDER_CONFIRM_PREFIX + tradeNo , "x" , 30 , TimeUnit.MINUTES);

        // 返回
        return orderConfirmVo;
    }

    @Override
    public OrderInfo findOrderInfoById(Long orderId) {

        // 获取用户的id
        UserAuthInfoVo userAuthInfo = UserAuthUtils.getUserAuthInfo();
        Long userId = userAuthInfo.getUserId();

        // 查询数据库
        LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        lambdaQueryWrapper.eq(OrderInfo::getUserId , userId) ;
        lambdaQueryWrapper.eq(OrderInfo::getId , orderId) ;
        OrderInfo orderInfo = orderInfoMapper.selectOne(lambdaQueryWrapper);

        // 返回
        return orderInfo;
    }


}
