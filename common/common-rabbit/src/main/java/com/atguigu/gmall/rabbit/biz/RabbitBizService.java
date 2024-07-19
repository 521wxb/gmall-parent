package com.atguigu.gmall.rabbit.biz;

import com.rabbitmq.client.Channel;

public interface RabbitBizService {

    /**
     * @param channel: 通道，通过该对象给rabbitmq服务端返回ack或者nack
     * @param deliveryTag：消息的标签
     * @param content：消息的内容
     * @param count: 规定的次数
     */
    public abstract void retry(Channel channel , long deliveryTag , String content , int count) ;

}
