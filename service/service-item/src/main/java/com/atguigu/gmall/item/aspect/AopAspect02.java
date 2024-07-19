package com.atguigu.gmall.item.aspect;

import com.atguigu.gmall.cache.anno.GmallCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
//@Aspect
public class AopAspect02 {

    // @Around("@annotation(com.atguigu.gmall.item.anno.GmallCache)")
    @Around("@annotation(gmallCache)")
    public Object around(ProceedingJoinPoint point , GmallCache gmallCache) throws Throwable {
        log.info("AopAspect02...around....前......");
        Object proceed = point.proceed();
        log.info(gmallCache.toString());
        log.info("AopAspect02...around....后......");
        return proceed ;
    }

}
