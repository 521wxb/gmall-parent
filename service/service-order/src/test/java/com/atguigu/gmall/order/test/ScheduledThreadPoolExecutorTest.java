package com.atguigu.gmall.order.test;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolExecutorTest {

    public static void main(String[] args) throws IOException {

        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5) ;
        scheduledThreadPoolExecutor.schedule(() -> {
            System.out.println("ScheduledThreadPoolExecutorTest延迟任务执行了" + new Date());
        } , 10 , TimeUnit.SECONDS) ;

        System.in.read() ;

    }

}
