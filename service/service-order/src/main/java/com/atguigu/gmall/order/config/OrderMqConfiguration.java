package com.atguigu.gmall.order.config;

import com.atguigu.gmall.rabbit.constant.MqConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 当使用RabbitTempalte发送消息的时候此时配置才会生效
 * 关于如下信息的声明可以使用@RabbitListener注解完成，并且使用@RabbitListener声明完毕以后，声明信息会立即生效
 */
@Configuration
public class OrderMqConfiguration {

    /**
     * 声明交换机
     */
    @Bean
    public Exchange orderExchange() {
        return ExchangeBuilder.directExchange(MqConstant.ORDER_TIMEOUT_EXCHANGE).durable(true).build() ;
    }

    /**
     * 声明队列
     */
    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(MqConstant.ORDER_TIMEOUT_QUEUE).deadLetterExchange(MqConstant.ORDER_TIMEOUT_EXCHANGE)
                .deadLetterRoutingKey(MqConstant.ORDER_TIMEOUT_CLOSE_ROUTINGKEY)
                .ttl(MqConstant.ORDER_TIMEOUT).build() ;
    }

    /**
     * 完成队列和交换机的绑定
     */
    @Bean
    public Binding orderQueueBinding() {
        Binding binding = BindingBuilder.bind(orderQueue()).to(orderExchange()).with(MqConstant.ORDER_ROUTINGKEY).noargs();
        return binding ;
    }

    /**
     * 声明死信队列
     */
    @Bean
    public Queue closeQueue() {
        Queue build = QueueBuilder.durable(MqConstant.ORDER_CLOSE_QUEUE).build();
        return build ;
    }

    /**
     * 把死信队列绑定到交换机上
     */
    @Bean
    public Binding closeQueueBinding() {
        return BindingBuilder.bind(closeQueue()).to(orderExchange()).with(MqConstant.ORDER_TIMEOUT_CLOSE_ROUTINGKEY).noargs() ;
    }

    /**
     * 声明支付成功以后更改订单状态的队列
     */
    @Bean
    public Queue payedQueue() {
        return QueueBuilder.durable(MqConstant.PAYED_QUEUE_NAME).build() ;
    }

    /**
     * 把payedQueue和orderExchange进行绑定
     */
    @Bean
    public Binding payedQueueBinding() {
        return BindingBuilder.bind(payedQueue()).to(orderExchange()).with(MqConstant.PAYED_ROUTING_KEY).noargs() ;
    }

}
