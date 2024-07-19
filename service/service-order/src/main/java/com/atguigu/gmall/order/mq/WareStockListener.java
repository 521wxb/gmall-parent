package com.atguigu.gmall.order.mq;

import com.atguigu.gmall.order.service.OrderInfoService;
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
public class WareStockListener {

    @Autowired
    private OrderInfoService orderInfoService ;

    @Autowired
    private RabbitBizService rabbitBizService ;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqConstant.WARE_ORDER_QUEUE_NAME , durable = "true") ,
            exchange = @Exchange(name = MqConstant.WARE_ORDER_EXCHANGE_NAME, type = "direct") ,
            key = {MqConstant.WARE_ORDER_ROUTING_KEY}
    ))
    public void wareStockListener(Message message , Channel channel) {

        // 获取消息
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        byte[] body = message.getBody();
        String msg = new String(body);

        try {

            // 调用service进行订单状态的修改
            orderInfoService.updateOrderStatusWare(msg) ;
            log.info("订单的状态修改完毕....");

            // 进行手动应答
            channel.basicAck(deliveryTag , true);

        } catch (IOException e) {
            e.printStackTrace();
            rabbitBizService.retry(channel , deliveryTag , msg , 3);
        }

    }

}
