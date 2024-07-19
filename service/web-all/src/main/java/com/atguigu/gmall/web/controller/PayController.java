package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.order.entity.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PayController {

    @Autowired
    private OrderFeignClient orderFeignClient ;

    @GetMapping(value = "/pay.html")
    public String pay(@RequestParam(value = "orderId") Long orderId , Model model) {
        Result<OrderInfo> orderInfoResult = orderFeignClient.findOrderInfoById(orderId);
        model.addAttribute("orderInfo" , orderInfoResult.getData()) ;
        return "payment/pay" ;
    }

    @GetMapping(value = "/pay/success.html")
    public String paySuccess() {
        return "payment/success.html" ;
    }

}
