package com.atguigu.gmall.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;

@Component
public class ScheduledTask {

    @Autowired
    private DelayQueue delayQueue ;

    @Scheduled(cron = "*/1 * * * * ?")
    public void getTask() {
        OrderDelayed poll = (OrderDelayed) delayQueue.poll();       // 拉取一个任务
        if(poll != null) {
            System.out.println(poll.getOrderId() + "执行延迟任务...");
        }
    }

}
