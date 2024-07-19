package com.atguigu.gmall.rabbit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMQConfiguration {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {

        // 创建一个RabbitTemplate对象
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory) ;

        // 给rabbitTemplate绑定confirm机制
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {

            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if(ack) {
                    log.info("消息投递给了交换机....成功了");
                }else {
                    log.error("消息投递给了交换机....失败了, {}", cause);
                }
            }

        });

        // 给rabbitTemplate绑定return机制
        rabbitTemplate.setMandatory(true);  // 将发送失败的消息再次发给生产者
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {

            // 当消息投递给队列的时候失败了，那么此时就需要执行该方法
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.error("replyCode: {} , replyText: {} , exchange: {} , routingKey: {} , message: {}" , replyCode , replyText , exchange , routingKey , new String(message.getBody()));
            }

        });

        return rabbitTemplate ;

    }

}
