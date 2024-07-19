package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gmall.payment.properties.AliPayProperties;
import com.atguigu.gmall.rabbit.constant.MqConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/payment/alipay")
public class OrderPayedController {

    @Autowired
    private AliPayProperties aliPayProperties ;

    @Autowired
    private RabbitTemplate rabbitTemplate ;

    @PostMapping(value = "/listenpayed")
    public String listenpayed(@RequestParam Map<String , String> params) {
        log.info("OrderPayedController...listenpayed...{} " ,  params);

        boolean verifyV1 = false ;
        /**
         * 对数据进行校验
         */
        try {
            verifyV1 = AlipaySignature.verifyV1(params, aliPayProperties.getAliPublicKey(), aliPayProperties.getCharSet(), aliPayProperties.getSignType());
            if(verifyV1) {      // 验签成功
                log.info("验签成功..." );
                rabbitTemplate.convertAndSend(MqConstant.ORDER_TIMEOUT_EXCHANGE , MqConstant.PAYED_ROUTING_KEY , JSON.toJSONString(params));
                return "success" ;

            } else {
                log.error("验签失败..." );
                return "error" ;
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            log.error("业务逻辑处理失败了..." );
            return "error" ;
        }

    }

}
