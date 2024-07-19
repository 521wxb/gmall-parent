package com.atguigu.gmall.cache.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)   // 当前这个自定义注解的使用位置为方法上
@Retention(value = RetentionPolicy.RUNTIME)  // 生效时期
public @interface GmallCache {

    /**
     * 缓存的key
     * @return
     */
    public String cacheKey() ;

    /**
     * 布隆过滤器的名称
     */
    public String bloomFilterName() default "";

    /**
     * 布隆过滤器要判断的值
     */
    public String bloomFilterValue() default "";

    /**
     * 分布式锁名称属性
     */
    public String lockName() default  "" ;

    /**
     * 是否启用分布式锁
     */
    public boolean enableLock() default false ;

}
