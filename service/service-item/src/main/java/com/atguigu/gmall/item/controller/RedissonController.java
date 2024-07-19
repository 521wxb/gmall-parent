package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.TimeUnit;




@RestController
@RequestMapping(value = "/redisson")
@Slf4j
public class RedissonController {

    @Autowired
    private RedissonClient redissonClient ;

    private String uuid ;

    RSemaphore semaphore = null ;   // 定义一个信号量的对象
    @PostConstruct
    public  void init() {
        semaphore = redissonClient.getSemaphore("test-semaphore");
        semaphore.addPermits(2);
    }

    @GetMapping(value = "/semaphore")
    public Result semaphore() throws InterruptedException {
        semaphore.acquire();  // 申请凭证
        log.info(Thread.currentThread().getName() + "----> 申请到了一个凭证...");
        Thread.sleep(10000);
        semaphore.release();
        log.info(Thread.currentThread().getName() + "----> 归还了一个凭证....");
        return Result.ok() ;
    }

    @GetMapping(value = "/read")
    public Result read() {
        log.info(Thread.currentThread().getId() + "进入到了read方法中");
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("item-read-write");
        RLock readLock = readWriteLock.readLock();      // 读
        readLock.lock();
        log.info(Thread.currentThread().getId() + "获取到了读锁进行数据的读操作.........");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info(Thread.currentThread().getId() + "对数据进行读操作 , uuid-->" + uuid);
        readLock.unlock();
        log.info(Thread.currentThread().getId() + "-----> 释放了读锁");

        return Result.ok() ;

    }

    @GetMapping(value = "/write")
    public Result write() {
        log.info(Thread.currentThread().getId() + "进入到了write方法中");
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("item-read-write");
        RLock writeLock = readWriteLock.writeLock() ; // 获取写锁对象
        writeLock.lock();
        log.info(Thread.currentThread().getId() + "获取到了写锁进行数据的写操作.........");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        uuid = UUID.randomUUID().toString().replace("-" , "") ;
        writeLock.unlock();
        log.info(Thread.currentThread().getId() + "-----> 释放了写锁");

        return Result.ok() ;

    }

    @GetMapping(value = "/lock2")
    public Result lock2() throws InterruptedException {

        //  获取RedissonClient对象
        RLock clientLock = redissonClient.getLock("item-lock");

        // 获取锁
        clientLock.lock();
        clientLock.lock();

        log.info(Thread.currentThread().getId() + "获取到了锁，执行业务操作...........");
        Thread.sleep(50000);         // 模拟业务处理时长
        log.info(Thread.currentThread().getId() + "业务操作执行完毕了................");

        // 释放锁
        clientLock.unlock();
        clientLock.unlock();

        return Result.ok() ;

    }

    @GetMapping(value = "/lock")
    public Result lock() throws InterruptedException {

        log.info(Thread.currentThread().getId() + "进入到了lock方法执行...........");

        //  获取RedissonClient对象
        RLock clientLock = redissonClient.getLock("item-lock");                     // 获取锁对象
        // clientLock.lock();          // 加锁，如果这个线程没有获取到，那么一直处于阻塞状态, 默认的失效时间为30s，但是Redisson框架会对锁进行续期
        // lock(long leaseTime, TimeUnit unit) : 显示的给锁设定了失效时间, 那么此时Redisson框架不会对锁进行续期
        // clientLock.lock(30 , TimeUnit.SECONDS);

         boolean tryLock = clientLock.tryLock();     // 尝试加锁，如果锁加上了，那么返回true；否则返回false。
        // boolean tryLock = clientLock.tryLock(5, 40, TimeUnit.SECONDS);  // 第一个参数表示的是获取锁的等待时长
        // boolean tryLock(long waitTime, TimeUnit unit) : 第一个参数表示的是获取锁的等待时长，第二个参数表示的是时间单位
        if(tryLock) {

            /**
             * 业务产生了异常以后，锁为什么还可以进行续期？   一个异步线程
             */
            try {
                // 执行业务操作
                log.info(Thread.currentThread().getId() + "获取到了锁，执行业务操作...........");
                Thread.sleep(50000);         // 模拟业务处理时长
                int a = 1 / 0 ;
                log.info(Thread.currentThread().getId() + "业务操作执行完毕了................");
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 释放锁
                clientLock.unlock();           // 释放锁
                log.info(Thread.currentThread().getId() + "业务操作执行完毕了，锁进行释放了....");
            }

        }else {
            log.info(Thread.currentThread().getId() + "没有获取到锁................");
        }

        // 返回数据
        return Result.ok() ;

    }

}
