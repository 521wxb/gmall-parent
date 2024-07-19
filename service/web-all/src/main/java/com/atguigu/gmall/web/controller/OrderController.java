package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderController {

    @Autowired
    private OrderFeignClient orderFeignClient ;

    @GetMapping(value = "/trade.html")
    public String trade(Model model) {

        // 远程调用service-order微服务的接口获取选中的购物车的数据
        Result<OrderConfirmVo> orderConfirmVoResult = orderFeignClient.orderConfirmData();
        OrderConfirmVo orderConfirmVo = orderConfirmVoResult.getData();

        // 从OrderConfirmVo对象中获取数据，将其存储到Model数据模型中
        model.addAttribute("detailArrayList" , orderConfirmVo.getDetailArrayList()) ;
        model.addAttribute("userAddressList" , orderConfirmVo.getUserAddressList()) ;
        model.addAttribute("totalNum" , orderConfirmVo.getTotalNum()) ;
        model.addAttribute("totalAmount" , orderConfirmVo.getTotalAmount()) ;
        model.addAttribute("tradeNo" , orderConfirmVo.getTradeNo()) ;

        // 返回
        return "order/trade" ;

    }

    @GetMapping(value = "/myOrder.html")
    public String myOrder() {
        return "order/myOrder" ;
    }

}
