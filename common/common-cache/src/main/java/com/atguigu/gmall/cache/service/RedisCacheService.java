package com.atguigu.gmall.cache.service;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * 操作redis的服务类
 */
public interface RedisCacheService {

    public abstract <T> T getCacheForObject(String key , Type type) ;

    /**
     * 从redis中获取数据
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public abstract <T> T getCacheForObject(String key , Class<T> clazz) ;

    /**
     * 保存数据的方法
     * @param key
     * @param data
     */
    public abstract void saveDataToRedis(String key, Object data) ;

    public abstract void saveDataToRedis(String key, Object data , Long timeOut , TimeUnit timeUnit) ;

}
