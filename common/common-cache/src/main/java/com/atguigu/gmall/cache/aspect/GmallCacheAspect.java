package com.atguigu.gmall.cache.aspect;

import com.atguigu.gmall.cache.anno.GmallCache;
import com.atguigu.gmall.cache.service.RedisCacheService;
import com.atguigu.gmall.common.constant.GmallConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

@Slf4j
@Aspect
public class GmallCacheAspect {

    @Autowired
    private RedissonClient redissonClient ;

    @Autowired
    private RedisCacheService redisCacheService ;

    @Around("@annotation(gmallCache)")
    public Object around(ProceedingJoinPoint point , GmallCache gmallCache) {

        // 获取目标方法参数
        // Long skuId = Long.parseLong(point.getArgs()[0].toString());

        // 获取bloomFilter，然后进行参数的判断
        String bloomFilterName = gmallCache.bloomFilterName();
        if(!StringUtils.isEmpty(bloomFilterName)) {
            RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(bloomFilterName);
            String bloomFilterValue = gmallCache.bloomFilterValue();
            if(!StringUtils.isEmpty(bloomFilterValue)) {
                Object value = paraseExpression(point, bloomFilterValue, Object.class);
                if(!bloomFilter.contains(value)) {
                    log.error("当前查询的商品在数据库中不存在,bloomFilter也不存在..........");
                    return null ;
                }
            }
        }

        // 获取redis的key
        String cacheKey = paraseExpression(point, gmallCache.cacheKey(), String.class);

        // 获取目标方法的返回值类型
        Type methodReturnType = getMethodType(point);

        // 查询redis
        Object obj = redisCacheService.getCacheForObject(cacheKey, methodReturnType);
        if(obj != null) {
            log.info(Thread.currentThread().getId() + "从redis中查询到了数据，并进行返回..........");
            return obj ;
        }

        // 获取是否需要启用分布式锁的注解属性值
        boolean enableLock = gmallCache.enableLock();
        if(enableLock) {        // 需要使用分布式锁

            // 获取分布式锁的名称
            String lockName = gmallCache.lockName();
            if(!StringUtils.isEmpty(lockName)) {

                // 对分布式锁的名称进行解析
                String redissonLockName = paraseExpression(point, lockName, String.class);
                RLock clientLock = redissonClient.getLock(redissonLockName); // 使用Redisson的分布式锁
                boolean isLock = clientLock.tryLock();
                if(isLock) {
                    log.info("获取到了分布式锁 ---> ThreadId: " + Thread.currentThread().getId());
                    try {

                        // 执行目标方法
                        obj = point.proceed();
                        if(obj == null) {
                            // 往redis中缓存一个x
                            log.error(Thread.currentThread().getId() + "从数据库中没有查询到数据，在redis中缓存了x");
                            redisCacheService.saveDataToRedis(cacheKey , GmallConstant.REDIS_NULL_VALUE);
                        }else {
                            // 把skuDetailVo存储到Redis中
                            log.info(Thread.currentThread().getId() + "从数据库中查询到数据，在redis中缓存了对应的数据");
                            redisCacheService.saveDataToRedis(cacheKey , obj);
                        }

                        return obj ;

                    }catch (Throwable e) {
                        e.printStackTrace();
                        throw new RuntimeException(e) ;         // 抛出异常
                    } finally {  // 释放分布式锁
                        clientLock.unlock();
                        log.info(Thread.currentThread().getId() + "----->释放了分布式锁");
                    }

                }else {

                    log.info("没有获取到了分布式锁 ---> ThreadId: " + Thread.currentThread().getId());

                    // 访问redis查询数据
                    obj = redisCacheService.getCacheForObject(cacheKey, methodReturnType);
                    if(obj != null) {
                        return obj ;
                    }else {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return redisCacheService.getCacheForObject(cacheKey, methodReturnType);
                    }

                }

            }
            else {

                // 直接调用目标方法，查询数据库
                try {

                    obj = point.proceed();            // 执行目标方法
                    if(obj == null) {
                        // 往redis中缓存一个x
                        log.error(Thread.currentThread().getId() + "从数据库中没有查询到数据，在redis中缓存了x");
                        redisCacheService.saveDataToRedis(cacheKey , GmallConstant.REDIS_NULL_VALUE);
                    }else {
                        // 把skuDetailVo存储到Redis中
                        log.info(Thread.currentThread().getId() + "从数据库中查询到数据，在redis中缓存了对应的数据");
                        redisCacheService.saveDataToRedis(cacheKey , obj);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                    throw new RuntimeException(e) ;
                }
                return obj ;

            }

        }else {     // 表示不需要使用分布式锁

            // 直接调用目标方法，查询数据库
            try {

                obj = point.proceed();            // 执行目标方法
                if(obj == null) {
                    // 往redis中缓存一个x
                    log.error(Thread.currentThread().getId() + "从数据库中没有查询到数据，在redis中缓存了x");
                    redisCacheService.saveDataToRedis(cacheKey , GmallConstant.REDIS_NULL_VALUE);
                }else {
                    // 把skuDetailVo存储到Redis中
                    log.info(Thread.currentThread().getId() + "从数据库中查询到数据，在redis中缓存了对应的数据");
                    redisCacheService.saveDataToRedis(cacheKey , obj);
                }
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e) ;
            }

            return obj ;

        }

    }

    /**
     * 获取目标方法的返回值类型
     * @return
     */
    public Type getMethodType(ProceedingJoinPoint point) {

        /**
         * 获取目标方法的返回值类型
         * 1、先获取目标方法对象
         * 2、然后在获取方法对象的返回值类型
         */
        MethodSignature signature = (MethodSignature) point.getSignature();     // 方法的签名对象，封装了方法的相关信息
        Method method = signature.getMethod();          // 获取目标方法对象
        // Class clazz = method.getReturnType();
        Type type = method.getGenericReturnType();
        return type ;

    }

    // 定义SpelExpressionParser对象, 是线程安全的
    private static final SpelExpressionParser spelExpressionParser = new SpelExpressionParser() ;

    // 进行spel表达式的解析
    public <T> T paraseExpression(ProceedingJoinPoint point , String cacheKey , Class<T> clazz) {

        // 获取表达式对象
        Expression expression = spelExpressionParser.parseExpression(cacheKey, ParserContext.TEMPLATE_EXPRESSION);

        // 创建EvaluationContext对象，预设值
        EvaluationContext evaluationContext = new StandardEvaluationContext() ;
        evaluationContext.setVariable("params" , point.getArgs());

        // 获取解析完毕以后表达式的值
        return expression.getValue(evaluationContext , clazz) ;

    }

}
