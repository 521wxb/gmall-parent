package com.atguigu.gmall.queue;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class OrderDelayed implements Delayed {      // 延迟任务

    private String orderId ;
    private Long delyTime ;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getDelyTime() {
        return delyTime;
    }

    public void setDelyTime(Long delyTime) {
        this.delyTime = delyTime;
    }

    public OrderDelayed(String orderId, Long delyTime) {
        this.orderId = orderId ;
        this.delyTime = System.currentTimeMillis() + delyTime * 1000 ;
    }

    /**
     * 当我们延迟队列中获取延迟任务的时候，那么此时就会调用该方法，获取延迟任务的时间
     * @param unit
     * @return
     */
    @Override
    public long getDelay(TimeUnit unit) {          // 返回延迟任务的延迟时间，每隔1s会执行一次
        System.out.println("OrderDelayed...getDelay..." + new Date());
        return delyTime - System.currentTimeMillis() ;   // 延迟任务的延迟时间3s
    }

    @Override
    public int compareTo(Delayed o) {
        return (int)(getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }

}
