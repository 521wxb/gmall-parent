package com.atguigu.gmall.queue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.DelayQueue;

@Configuration
public class DelayQueueConfiguration {

    @Bean
    public DelayQueue<OrderDelayed> delayQueue() {
        return new DelayQueue<>() ;
    }

}
