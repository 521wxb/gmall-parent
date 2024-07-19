package com.atguigu.gmall.cache.anno;

import com.atguigu.gmall.cache.aspect.GmallCacheAspect;
import com.atguigu.gmall.cache.config.RedissonConfiguration;
import com.atguigu.gmall.cache.service.impl.RedisCacheServiceImpl;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)           // 当前这个自定义注解的使用位置为启动类
@Retention(value = RetentionPolicy.RUNTIME)  // 生效时期
@Import(value = {GmallCacheAspect.class , RedisCacheServiceImpl.class , RedissonConfiguration.class})
public @interface EnableGmallCacheAspect {


}
