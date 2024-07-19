package com.atguigu.gmall.queue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.util.concurrent.DelayQueue;

@SpringBootApplication
@EnableScheduling
public class DelayQueueApplication {

    public static void main(String[] args) throws IOException {

        // 返回spring容器
        ConfigurableApplicationContext applicationContext = SpringApplication.run(DelayQueueApplication.class, args);

        // 从spring容器中获取延迟队列对象
        DelayQueue delayQueue = applicationContext.getBean(DelayQueue.class);

        // 向延迟队列中添加延迟任务
        delayQueue.add(new OrderDelayed("1234" , 5L)) ;  // 5s、1234表示的是延迟任务的id

        // System.in.read() ;

    }

}
