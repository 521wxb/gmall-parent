package com.atguigu.gmall.seckill.mq;

import com.atguigu.gmall.rabbit.biz.RabbitBizService;
import com.atguigu.gmall.rabbit.constant.MqConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class SeckillQueueListener {

    @Autowired
    private RabbitBizService rabbitBizService ;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqConstant.SECKILL_ORDER_QUEUE_NAME , durable = "true") ,
            exchange = @Exchange(name = MqConstant.SECKILL_ORDER_QUEUE_EXCHANGE , type = "direct") ,
            key = {MqConstant.SECKILL_ORDER_QUEUE_ROUTING_KEY}
    ))
    public void seckillQueueListener(Message message , Channel channel) {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        byte[] body = message.getBody();
        String msg = new String(body) ;
        log.info("获取到了秒杀订单的消息...{}" , msg);

        try {

            // TODO 进行业务处理
            channel.basicAck(deliveryTag , true);

        } catch (IOException e) {
            e.printStackTrace();
            rabbitBizService.retry(channel , deliveryTag , msg , 3);
        }

    }

}
