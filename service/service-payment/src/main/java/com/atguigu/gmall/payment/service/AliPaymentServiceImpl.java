package com.atguigu.gmall.payment.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.payment.properties.AliPayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AliPaymentServiceImpl implements AliPaymentService{

    @Autowired
    private AlipayClient alipayClient ;

    @Autowired
    private AliPayProperties aliPayProperties ;

    @Autowired
    private OrderFeignClient orderFeignClient ;

    @Override
    public String generateOrderPayPage(Long orderId) {

        // 发送请求
        String result = null;
        try {

            // 创建AlipayTradePagePayRequest对象
            AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest() ;

            // 给AlipayTradePagePayRequest设置参数
            alipayTradePagePayRequest.setNotifyUrl(aliPayProperties.getNotifyUrl());
            alipayTradePagePayRequest.setReturnUrl(aliPayProperties.getReturnUrl());

            // 远程调用后期订单信息
            Result<OrderInfo> orderInfoResult = orderFeignClient.findOrderInfoById(orderId);
            OrderInfo orderInfo = orderInfoResult.getData();

            // 定义一个Map，封装相关参数
            Map<String , String> params = new HashMap<>() ;
            params.put("out_trade_no" , orderInfo.getOutTradeNo()) ;
            params.put("total_amount" , String.valueOf(orderInfo.getTotalAmount().doubleValue())) ;
            params.put("subject" , orderInfo.getTradeBody()) ;
            params.put("product_code" , "FAST_INSTANT_TRADE_PAY") ;
            String timeExpireStr = DateUtil.format(orderInfo.getExpireTime(), "yyyy-MM-dd HH:mm:ss");
            params.put("time_expire" , timeExpireStr) ;

            // 把参数设置给alipayTradePagePayRequest
            alipayTradePagePayRequest.setBizContent(JSON.toJSONString(params));

            // 发送请求得到结果
            result = alipayClient.pageExecute(alipayTradePagePayRequest).getBody();

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return result;
    }
}
