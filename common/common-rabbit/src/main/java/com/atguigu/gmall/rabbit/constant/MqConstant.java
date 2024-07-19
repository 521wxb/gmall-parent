package com.atguigu.gmall.rabbit.constant;

public interface MqConstant {

    /**
     * 定义订单超时未支付的交换机
     */
    public static final String ORDER_TIMEOUT_EXCHANGE = "order.exchange" ;

    /**
     * 定义订单超时未支付的队列
     */
    public static final String ORDER_TIMEOUT_QUEUE = "order.queue" ;

    /**
     * 定义死信队列的routingKey
     */
    public static final String ORDER_TIMEOUT_CLOSE_ROUTINGKEY = "close" ;

    /**
     * 定时的超时时间
     */
    public static final Integer ORDER_TIMEOUT = 1000 * 30 * 60 ;

    /**
     * 定义订单队列的bindKey
     */
    public static final String ORDER_ROUTINGKEY = "order" ;

    /**
     * 定义关闭订单的队列名称
     */
    public static final String ORDER_CLOSE_QUEUE = "close.queue" ;

    /**
     * 定义消息重试次数
     */
    public static final String REDIS_MSG_COUNT = "msg:count:" ;

    /**
     * 定义支付成功以后更改订单状态的队列名称
     */
    public static final String PAYED_QUEUE_NAME = "order.payed.queue" ;

    /**
     * 定义支付成功以后更改订单状态routingKey
     */
    public static final String PAYED_ROUTING_KEY = "payed" ;

    /**
     * 定义扣减库存的交换机的名称
     */
    public static final String WARE_STOCK_EXCHANGE_NAME = "exchange.direct.ware.stock" ;

    /**
     * 定义扣减库存的routingKey
     */
    public static final String WARE_STOCK_ROUTING_KEY = "ware.stock" ;

    /**
     * 定义扣减库存成以后通过订单系统的队列名称
     */
    public static final String WARE_ORDER_QUEUE_NAME = "queue.ware.order" ;

    /**
     * 定义扣减库存成功以后通知订单系统的交换机名称
     */
    public static final String WARE_ORDER_EXCHANGE_NAME = "exchange.direct.ware.order" ;

    /**
     * 定义扣减库存成功以后通过订单提供的routingKey
     */
    public static final String WARE_ORDER_ROUTING_KEY = "ware.order" ;

    /**
     * 定义秒杀排队的MQ的交换机的名称
     */
    public static final String SECKILL_ORDER_QUEUE_EXCHANGE = "seckill.order.exchange"  ;

    /**
     * 定义秒杀排队的MQ的routingKey的名称
     */
    public static final String SECKILL_ORDER_QUEUE_ROUTING_KEY = "seckill.order"  ;

    /**
     * 定义秒杀排队的MQ的队列的名称
     */
    public static final String SECKILL_ORDER_QUEUE_NAME = "seckill.order.queue"  ;

}
