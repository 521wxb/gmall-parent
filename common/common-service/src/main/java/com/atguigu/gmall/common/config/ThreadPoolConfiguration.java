package com.atguigu.gmall.common.config;

import com.atguigu.gmall.common.properties.ThreadPoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
@EnableConfigurationProperties(value = ThreadPoolProperties.class)
public class ThreadPoolConfiguration {

    @Autowired
    private ThreadPoolProperties threadPoolProperties ;

    @Value("${spring.application.name}")
    private String applicationName ;

    /**
     * 配置一个线程池
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {

        // 创建一个ThreadPoolExecutor对象
        /**
           int corePoolSize: 核心线程数
           int maximumPoolSize：最大线程数
           long keepAliveTime：临时线程最大的空闲时间
           TimeUnit unit：时间单位
           BlockingQueue<Runnable> workQueue：任务队列
           ThreadFactory：线程工厂
           RejectedExecutionHandler handler：任务的拒绝策略
         */
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(threadPoolProperties.getCorePoolSize()
                , threadPoolProperties.getMaximumPoolSize(),
                threadPoolProperties.getKeepAliveTime(), TimeUnit.MINUTES, new ArrayBlockingQueue<>(threadPoolProperties.getWorkQueueSize()),
                new ThreadFactory() {
                    int num = 1 ;       // 计数器，用来标识线程名称
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("thread-【" + applicationName +"】-" + num++);
                        return thread;
                    }

                }, new ThreadPoolExecutor.AbortPolicy()) ;

        return threadPoolExecutor ;
    }

}
