package com.atguigu.gmall.order.test;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import com.atguigu.gmall.order.OrderApplication;
import com.atguigu.gmall.order.entity.OrderDetail;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.entity.OrderStatusLog;
import com.atguigu.gmall.order.entity.PaymentInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.mapper.OrderStatusLogMapper;
import com.atguigu.gmall.order.mapper.PaymentInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = OrderApplication.class)
public class OrderApplicationTest {

    @Autowired
    private OrderInfoMapper orderInfoMapper ;

    @Autowired
    private OrderDetailMapper orderDetailMapper ;

    @Autowired
    private OrderStatusLogMapper orderStatusLogMapper ;

    @Autowired
    private PaymentInfoMapper paymentInfoMapper ;

    @Test
    public void savePaymentInfo() {
        PaymentInfo paymentInfo = new PaymentInfo() ;
        paymentInfo.setUserId(3L);
        paymentInfo.setTradeNo(UUID.randomUUID().toString().replace("-" , ""));
        paymentInfoMapper.insert(paymentInfo) ;
    }

    @Test
    public void saveOrderStatusLog() {
        OrderStatusLog orderStatusLog = new OrderStatusLog() ;
        orderStatusLog.setUserId(3L);
        orderStatusLog.setOperateTime(new Date());
        orderStatusLogMapper.insert(orderStatusLog) ;
    }

    @Test
    public void saveOrder() {
        OrderInfo orderInfo = new OrderInfo() ;
        orderInfo.setConsignee("尚硅谷");
        orderInfo.setTotalAmount(new BigDecimal("100"));
        orderInfo.setUserId(3L);
        orderInfoMapper.insert(orderInfo) ;
    }

    @Test
    public void saveOrderDetail() {
        OrderDetail orderDetail = new OrderDetail() ;
        orderDetail.setUserId(3L);
        orderDetail.setSkuName("尚硅谷");
        orderDetailMapper.insert(orderDetail) ;
    }

}
