package com.atguigu.gmall.cache.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cache.service.RedisCacheService;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

public class RedisCacheServiceImpl implements RedisCacheService {

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Override
    public Object getCacheForObject(String key, Type type) {
        String redisJsonData = redisTemplate.opsForValue().get(key);
        if(GmallConstant.REDIS_NULL_VALUE.equals(redisJsonData)) { // 数据是X
            throw new GmallException(ResultCodeEnum.REDIS_SKU_DETAIL_ERROR) ;
        }
        if(StringUtils.isEmpty(redisJsonData)) {        // redis中不存在数据
            return null ;
        }
        Object object = JSON.parseObject(redisJsonData, type);
        return object ;
    }

    /**
     * redis中存储是x
     * redis中存储的null，没有数据
     * redis中存储的真实数据
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T getCacheForObject(String key, Class<T> clazz) {
        String redisJsonData = redisTemplate.opsForValue().get(key);
        if(GmallConstant.REDIS_NULL_VALUE.equals(redisJsonData)) { // 数据是X
            throw new GmallException(ResultCodeEnum.REDIS_SKU_DETAIL_ERROR) ;
        }

        if(StringUtils.isEmpty(redisJsonData)) {        // redis中不存在数据
            return null ;
        }
        T result = JSON.parseObject(redisJsonData, clazz);      // 把json数据转换成指定的对象进行返回
        return result;
    }

    @Override
    public void saveDataToRedis(String key, Object data) {
        if(GmallConstant.REDIS_NULL_VALUE.equals(data.toString())) {        // 数据是X
            redisTemplate.opsForValue().set(key , GmallConstant.REDIS_NULL_VALUE);
        }else {
            String jsonData = JSON.toJSONString(data);
            redisTemplate.opsForValue().set(key , jsonData);
        }
    }

    @Override
    public void saveDataToRedis(String key, Object data, Long timeOut, TimeUnit timeUnit) {
        if(GmallConstant.REDIS_NULL_VALUE.equals(data.toString())) {        // 数据是X
            redisTemplate.opsForValue().set(key , GmallConstant.REDIS_NULL_VALUE , timeOut , timeUnit);
        }else {
            String jsonData = JSON.toJSONString(data);
            redisTemplate.opsForValue().set(key , jsonData , timeOut , timeUnit);
        }
    }

}
