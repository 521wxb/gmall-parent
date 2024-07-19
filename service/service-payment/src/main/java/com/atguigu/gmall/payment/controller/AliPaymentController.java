package com.atguigu.gmall.payment.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.payment.service.AliPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/payment")
public class AliPaymentController {

    @Autowired
    private AliPaymentService aliPaymentService ;

    @GetMapping(value = "/alipay/submit/{orderId}")
    public String aliPayment(@PathVariable(value = "orderId") Long orderId) {
        log.info("AliPaymentController...aliPayment方法执行了,订单的id为:{}" , orderId);
        String result = aliPaymentService.generateOrderPayPage(orderId);
        return result ;
    }

}
