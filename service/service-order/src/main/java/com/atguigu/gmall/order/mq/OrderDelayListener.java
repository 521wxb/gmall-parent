package com.atguigu.gmall.order.mq;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.order.biz.OrderBizService;
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
import java.util.Map;

@Slf4j
@Component
public class OrderDelayListener {

    @Autowired
    private RabbitBizService rabbitBizService ;

    @Autowired
    private OrderInfoService orderInfoService ;

    @RabbitListener(queues = MqConstant.ORDER_CLOSE_QUEUE)
    public void closeQueueListener(Message message , Channel channel) throws IOException {

        // 获取到消息的信息
        long deliveryTag = message.getMessageProperties().getDeliveryTag();     // 获取消息的标签
        byte[] body = message.getBody();
        String msg = new String(body);
        log.info("deliveryTag： {} , message: {}" , deliveryTag , msg);

        try {

            // 进行关闭订单的操作
            Map map = JSON.parseObject(msg, Map.class);
            Long userId = Long.parseLong(map.get("userId").toString()) ;
            Long orderId = Long.parseLong(map.get("orderId").toString()) ;
            orderInfoService.closeOrder(userId , orderId) ;

            // 成功此时就返回ack
            channel.basicAck(deliveryTag , true);

        }catch (Exception e) {
            // e.printStackTrace();
            rabbitBizService.retry(channel , deliveryTag , msg , 3);
        }

    }

}
