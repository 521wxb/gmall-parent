package com.atguigu.gmall.seckill.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.entity.UserAuthInfoVo;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.UserAuthUtils;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.rabbit.constant.MqConstant;
import com.atguigu.gmall.seckill.biz.SeckillBizService;
import com.atguigu.gmall.seckill.vo.SeckillQueueMsg;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.seckill.entity.SeckillGoods;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
* @author Administrator
* @description 针对表【seckill_goods】的数据库操作Service实现
* @createDate 2023-03-04 09:06:15
*/
@Slf4j
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements SeckillGoodsService{

    @Autowired
    private SeckillBizService seckillBizService ;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper ;

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Autowired
    private RabbitTemplate rabbitTemplate ;

    @Autowired
    private RedissonClient redissonClient;

    // 创建一个Map作为本地缓存
    public static final ConcurrentHashMap<Long , SeckillGoods> localCacheMap = new ConcurrentHashMap<>() ;

    @Override
    public List<SeckillGoods> selectAllToDaySeckillGoods(Date date) {
        String dateStr = DateUtil.format(date, "yyyy-MM-dd");
        return seckillGoodsMapper.selectAllToDaySeckillGoods(dateStr) ;
    }

    @Override
    public void saveSeckillGoodsToRedis(Date date , List<SeckillGoods> seckillGoodsList) {

        RLock lock = redissonClient.getLock(GmallConstant.REDIS_CACEH_PRE_HOST);
        boolean tryLock = lock.tryLock();
        if(tryLock) {
            log.info(Thread.currentThread().getId() + "获取到了分布式锁，开始进行存储数据到Redis...");
            try {

                /**
                 * 思考：应该使用哪一种数据类型存储秒杀商品数据?
                 * string:    seckill:goods:2023-03-04     所有的秒杀商品所对应的集合
                 * hash:      seckill:goods:2023-03-04   skuId 秒杀商品的详情数据
                 */
                String dateStr = DateUtil.format(date, "yyyy-MM-dd");
                String cacheKey = GmallConstant.REDIS_SECKILL_GOODS_PREFIX + dateStr ;

                // 存储数据到Redis中
                seckillGoodsList.stream().forEach(seckillGoods -> {
                    redisTemplate.opsForHash().put(cacheKey , String.valueOf(seckillGoods.getSkuId()) , JSON.toJSONString(seckillGoods));

                    // 把商品的库存数据存储到Redis中
                    redisTemplate.opsForValue()
                            .set(GmallConstant.REDIS_SECKILL_GOODS_STOCK_COUNT_PREFIX + seckillGoods.getSkuId() ,
                                    String.valueOf(seckillGoods.getStockCount() * 3),
                                    2 , TimeUnit.DAYS);

                });

                // 设置过期时间
                redisTemplate.expire(cacheKey , 2 , TimeUnit.DAYS) ;

            }catch (Exception e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
                log.info(Thread.currentThread().getId() + "释放了分布式锁...");
            }

        }

    }

    @Override
    public void saveSeckillGoodsToLocalCache(List<SeckillGoods> seckillGoodsList) {
        localCacheMap.clear();
        seckillGoodsList.stream().forEach(seckillGoods -> {
            localCacheMap.put(seckillGoods.getSkuId() , seckillGoods) ;
        });
    }

    @Override
    public String genSeckillCode(Long skuId) {

        //  从本地缓存中进行数据的查询
        SeckillGoods seckillGoods = seckillBizService.findSeckillGoodsBySkuId(skuId);

        // 判断开始时间
        Date currentDate = new Date() ;
        Date startTime = seckillGoods.getStartTime();
        if(startTime.getTime() > currentDate.getTime()) {
            throw new GmallException(ResultCodeEnum.SECKILL_NO_START) ;
        }

        // 判断结束时间
        if(seckillGoods.getEndTime().getTime() < currentDate.getTime()) {
            throw new GmallException(ResultCodeEnum.SECKILL_END) ;
        }

        // 判断是否存在库存
        if(seckillGoods.getStockCount() <= 0) {
            throw new GmallException(ResultCodeEnum.SECKILL_FINISH) ;
        }

        // 生成秒杀码
        UserAuthInfoVo userAuthInfo = UserAuthUtils.getUserAuthInfo();
        Long userId = userAuthInfo.getUserId();

        // 生成秒杀码
        String seckillCode = generateSeckillCode(userId , skuId) ;

        // 把秒杀码存储到Redis中，如果Redis中已经存在这个用户的秒杀码了就不需要再次进行存储
        String redisSeckillCodeKey = GmallConstant.REDIS_SECKILL_CODE_PREFIX + seckillCode ;
        if(!redisTemplate.hasKey(redisSeckillCodeKey)) {
            redisTemplate.opsForValue().set(redisSeckillCodeKey , "0" , 1 , TimeUnit.DAYS);
        }

        // 返回
        return seckillCode;
    }

    @Override
    public void queue(Long skuId, String skuIdStr) {

        // 进行校验秒杀码是否是当前登录用户的
        UserAuthInfoVo userAuthInfo = UserAuthUtils.getUserAuthInfo();
        Long userId = userAuthInfo.getUserId();
        String seckillCode = generateSeckillCode(userId, skuId);
        if(!seckillCode.equals(skuIdStr)) {
            throw new GmallException(ResultCodeEnum.SECKILL_ILLEGAL) ;
        }

        // 秒杀码是否是通过正常途径生成的
        String redisSeckillCodeKey = GmallConstant.REDIS_SECKILL_CODE_PREFIX + skuIdStr ;
        if(!redisTemplate.hasKey(redisSeckillCodeKey)) {
            throw new GmallException(ResultCodeEnum.SECKILL_ILLEGAL) ;
        }

        //  从本地缓存中进行数据的查询
        SeckillGoods seckillGoods = seckillBizService.findSeckillGoodsBySkuId(skuId);

        // 判断开始时间
        Date currentDate = new Date() ;
        Date startTime = seckillGoods.getStartTime();
        if(startTime.getTime() > currentDate.getTime()) {
            throw new GmallException(ResultCodeEnum.SECKILL_NO_START) ;
        }

        // 判断结束时间
        if(seckillGoods.getEndTime().getTime() < currentDate.getTime()) {
            throw new GmallException(ResultCodeEnum.SECKILL_END) ;
        }

        // 判断是否存在库存
        if(seckillGoods.getStockCount() <= 0) {
            throw new GmallException(ResultCodeEnum.SECKILL_FINISH) ;
        }

        /**
         * 判断用户是否已经排过队了,基于秒杀码和Redis进行判断
         */
        Long increment = redisTemplate.opsForValue().increment(GmallConstant.REDIS_SECKILL_CODE_PREFIX + skuIdStr);
        if(increment <= 1) {

            /**
             * 预扣库存：主要的目的就是将大量请求拦截到排队之前
             */
            seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);

            /**
             * 从Redis中进行预扣库存
             */
            Long decrement = redisTemplate.opsForValue().decrement(GmallConstant.REDIS_SECKILL_GOODS_STOCK_COUNT_PREFIX + seckillGoods.getSkuId());
            if(decrement < 0 ) {
                throw new GmallException(ResultCodeEnum.SECKILL_FINISH) ;
            }

            // 发送消息MQ进行下单请求排队: 要发送的消息内容：userId 、skuId、seckillCode
            SeckillQueueMsg seckillQueueMsg = new SeckillQueueMsg() ;
            seckillQueueMsg.setSkuId(skuId);
            seckillQueueMsg.setUserId(userId);
            seckillQueueMsg.setSeckillCode(skuIdStr) ;

            // 发送消息
            rabbitTemplate.convertAndSend(MqConstant.SECKILL_ORDER_QUEUE_EXCHANGE, MqConstant.SECKILL_ORDER_QUEUE_ROUTING_KEY , JSON.toJSONString(seckillQueueMsg));
        }

    }

    @Override
    public ResultCodeEnum checkOrderStatus(Long skuId) {

        // 获取秒杀码
        Long userId = UserAuthUtils.getUserAuthInfo().getUserId();
        String seckillCode = generateSeckillCode(userId, skuId);

        // 构建Redis中当前用户的秒杀订单的key
        String redisOrderKey = GmallConstant.REDIS_SECKILL_ORDER_PREFIX + seckillCode ;
        String orderInfoJson = redisTemplate.opsForValue().get(redisOrderKey);
        if(StringUtils.isEmpty(orderInfoJson)) {        // 在排队
            return ResultCodeEnum.SECKILL_RUN ;
        }else {
            if(GmallConstant.REDIS_SECKILL_ORDER_NO_STOCK.equalsIgnoreCase(orderInfoJson)) {
                return ResultCodeEnum.SECKILL_FINISH ;
            }else {
                OrderInfo orderInfo = JSON.parseObject(orderInfoJson, OrderInfo.class);
                if(orderInfo.getId() == null) {  // 215
                    return ResultCodeEnum.SECKILL_SUCCESS ;
                }else {         // 218
                    return ResultCodeEnum.SECKILL_ORDER_SUCCESS ;
                }
            }
        }
    }

    /**
     * 生成秒杀码
     * @param userId
     * @param skuId
     * @return
     */
    private String generateSeckillCode(Long userId, Long skuId) {
        Date date = new Date() ;
        String dateStr = DateUtil.format(date, "yyyy-MM-dd");
        String seckillCodeStr = dateStr + "-" + userId + "-" + skuId ;
        String seckillCode = DigestUtils.md5DigestAsHex(seckillCodeStr.getBytes(StandardCharsets.UTF_8));
        return seckillCode ;
    }

}




