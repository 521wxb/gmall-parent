package com.atguigu.gmall.seckill.biz.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.seckill.biz.SeckillBizService;
import com.atguigu.gmall.seckill.entity.SeckillGoods;
import com.atguigu.gmall.seckill.service.impl.SeckillGoodsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SeckillBizServiceImpl implements SeckillBizService {

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Override
    public List<SeckillGoods> findAllToDaySeckillGoods() {

        // 从本地缓存中进行查询
        ConcurrentHashMap<Long, SeckillGoods> localCacheMap = SeckillGoodsServiceImpl.localCacheMap;
        if(localCacheMap == null || localCacheMap.size() == 0) {

            // 查询redis
            String dateStr = DateUtil.format(new Date(), "yyyy-MM-dd");
            String cacheKey = GmallConstant.REDIS_SECKILL_GOODS_PREFIX + dateStr ;
            List<Object> values = redisTemplate.opsForHash().values(cacheKey);

            // 对redis中查询到的数据进行处理，把数据存储到本地缓存中
            List<SeckillGoods> seckillGoodsList = values.stream().map(obj -> {
                String dataJson = obj.toString();
                SeckillGoods seckillGoods = JSON.parseObject(dataJson, SeckillGoods.class);
                localCacheMap.put(seckillGoods.getSkuId(), seckillGoods);
                return seckillGoods;
            }).collect(Collectors.toList());

            // 进行数据的返回
            return seckillGoodsList ;

        } else {
            Collection<SeckillGoods> values = localCacheMap.values();
            List<SeckillGoods> seckillGoodsList = values.stream().collect(Collectors.toList());
            return seckillGoodsList ;
        }

    }

    @Override
    public SeckillGoods findSeckillGoodsBySkuId(Long skuId) {

        // 从本地缓存中先进行查询
        ConcurrentHashMap<Long, SeckillGoods> localCacheMap = SeckillGoodsServiceImpl.localCacheMap;
        SeckillGoods seckillGoods = localCacheMap.get(skuId);
        if ( seckillGoods != null) {            // 如果本地缓存中是存在数据, 直接进行返回
            return seckillGoods ;
        }

        // 本地缓存中没有数据，查询redis，把redis中的数据存储到本地缓存
        // 查询redis
        String dateStr = DateUtil.format(new Date(), "yyyy-MM-dd");
        String cacheKey = GmallConstant.REDIS_SECKILL_GOODS_PREFIX + dateStr ;
        List<Object> values = redisTemplate.opsForHash().values(cacheKey);

        // 对redis中查询到的数据进行处理，把数据存储到本地缓存中
        List<SeckillGoods> collect = values.stream().map(obj -> {
            String dataJson = obj.toString();
            SeckillGoods seckillGoodsRedis = JSON.parseObject(dataJson, SeckillGoods.class);
            return seckillGoodsRedis;
        }).collect(Collectors.toList());

        collect.forEach(seckillGoods1 -> {
            localCacheMap.put(seckillGoods1.getSkuId() , seckillGoods1) ;
        });


        // 从本地缓存中查询，返回
        return localCacheMap.get(skuId) ;
    }
}
