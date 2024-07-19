package com.atguigu.gmall.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping(value = "/hello")
public class HelloController {

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    // 创建一个锁对象
    // private static final Lock lock = new ReentrantLock() ;

    @GetMapping(value = "/count")
    public String count() {

        // 加锁
        // lock.lock();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        lock(uuid) ;
        try {
            incr() ;
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            unLock(uuid) ;
        }
        // lock.unlock();

        return "ok" ;

    }

    // 解锁方法
    private void unLock(String uuid) {
//        String redisLockValue = redisTemplate.opsForValue().get("item-lock");
//        if(uuid.equals(redisLockValue)) {
//            redisTemplate.delete("item-lock") ;
//        }

        // 定义lua脚本
        String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                "then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end" ;

        // 执行lua脚本
        /**
         * RedisScript<T> script: 封装了lua脚本的一个对象
         * List<K> keys：操作的key的集合，这个集合中的元素的索引是从1开始
         * Object... args：表示的就是执行lua脚本的参数，参数的索引是从1开始
         */
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList("item-lock"), uuid);
        if(result == 1) {
            log.info("删除了自己的锁对象....");
        }else  {
            log.info("锁是别人的，删除失败.........");
        }

    }

    // 加锁方法
    public void lock(String uuid) {

        // 获取锁并且设置锁的过期时间为30s
        while(!redisTemplate.opsForValue().setIfAbsent("item-lock", uuid , 30 , TimeUnit.SECONDS)) {}
        // redisTemplate.expire("item-lock" , 30 , TimeUnit.SECONDS) ; // 给锁设置过期时间

        // 定时任务或者守护线程，对锁进行续期，每1s给锁进行一次续期，续期到设置锁的过期时间(看门狗)
        Thread thread = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                redisTemplate.expire("item-lock" , 30 , TimeUnit.SECONDS) ;
            }
        });
        thread.setDaemon(true);  // 设置该线程为守护线程
        thread.start();

    }

    // 业务方法
    private void incr() {

        try {
            Thread.sleep(40000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String count = redisTemplate.opsForValue().get("count");
        if(StringUtils.isEmpty(count)) {
            redisTemplate.opsForValue().set("count" , "1");
        }else {
            int parseInt = Integer.parseInt(count);
            parseInt++ ;
            redisTemplate.opsForValue().set("count" , String.valueOf(parseInt));
        }

    }

}
