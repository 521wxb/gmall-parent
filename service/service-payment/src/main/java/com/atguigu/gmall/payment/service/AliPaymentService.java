package com.atguigu.gmall.payment.service;

public interface AliPaymentService {

    /**
     * 请求支付宝开放平台接口获取支付宝收银台字符串
     * @param orderId
     * @return
     */
    public abstract String generateOrderPayPage(Long orderId) ;

}
