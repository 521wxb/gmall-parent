package com.atguigu.gmall.order.mq;

import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.rabbit.biz.RabbitBizService;
import com.atguigu.gmall.rabbit.constant.MqConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OrderPayedListener {

    @Autowired
    private OrderInfoService orderInfoService ;

    @Autowired
    private RabbitBizService rabbitBizService;

    @RabbitListener(queues = MqConstant.PAYED_QUEUE_NAME)
    public void orderPayedQueueListener(Message message , Channel channel) {

        // 获取消息的正文
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        byte[] body = message.getBody();
        String messageBody = new String(body);
        log.info("OrderPayedListener...orderPayedQueueListener...获取到消息：{}" , messageBody);

        try {

            // 调用
            orderInfoService.orderPayedStatus(messageBody) ;
            log.info("订单的状态修改完毕...");

            // 进行手动应答
            channel.basicAck(deliveryTag , true);

        } catch (IOException e) {
            e.printStackTrace();
            rabbitBizService.retry(channel , deliveryTag , messageBody , 3 );       // 消息处理失败以后进行再次消费
        }

    }

}
