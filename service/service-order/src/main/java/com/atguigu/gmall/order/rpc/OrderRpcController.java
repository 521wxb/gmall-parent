package com.atguigu.gmall.order.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/inner/order")
public class OrderRpcController {

    @Autowired
    private OrderBizService orderBizService ;

    @GetMapping(value = "/orderConfirmData")
    public Result<OrderConfirmVo> orderConfirmData() {
        OrderConfirmVo orderConfirmVo = orderBizService.orderConfirmData() ;
        return Result.build(orderConfirmVo , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/findOrderInfoById")
    public Result<OrderInfo> findOrderInfoById(@RequestParam(value = "orderId") Long orderId) {
        OrderInfo orderInfo = orderBizService.findOrderInfoById(orderId) ;
        return Result.build(orderInfo , ResultCodeEnum.SUCCESS) ;
    }

}
