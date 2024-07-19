package com.atguigu.gmall.item.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
//@Component      // 把该类纳入到spring容器中
//@Aspect         // 声明当前类是一个切面类
public class AopAspect01 {

    //@Before(value = "execution( * com.atguigu.gmall.item.service.*.*(..))")
    public void before() {
        log.info("AopAspect01....before...............");
    }

    /**
     * 不论业务方法是否正常返回都会执行
     */
    // @After(value = "execution( * com.atguigu.gmall.item.service.*.*(..))")
    public void after() {
        log.info("AopAspect01....after...............");
    }

    /**
     * 业务方法正常返回以后，那么此时执行该方法
     */
    // @AfterReturning(value = "execution( * com.atguigu.gmall.item.service.*.*(..))")
    public void afterReturning() {
        log.info("AopAspect01....afterReturning...............");
    }

    // @AfterThrowing(value = "execution( * com.atguigu.gmall.item.service.*.*(..))")
    public void afterThrowing() {
        log.info("AopAspect01....afterThrowing...............");
    }

    @Around(value = "execution( * com.atguigu.gmall.item.service.*.*(..))")
    public Object around(ProceedingJoinPoint point) {

        log.info("AopAspect01....around前置方法...............");
        Object proceed = null ;
        try {
            proceed = point.proceed();  // 执行目标方法
            log.info("AopAspect01....around返回后通知方法....afterReturning...........");
        }catch (Throwable e) {          // 在aop切面中进行了异常的捕获，然后打印了异常的信息，没有做其他的事情，合理不？不合理
            e.printStackTrace();
            log.info("AopAspect01....异常通知....afterThrowing...........");
        } finally {
            log.info("AopAspect01....后置通知....after...........");
        }

        System.out.println(proceed);
        return proceed ;
    }

}
