package com.atguigu.gmall.feign.order;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.fallback.OrderFeignClientFallback;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "service-order" , fallback = OrderFeignClientFallback.class)
public interface OrderFeignClient {

    @GetMapping(value = "/api/inner/order/orderConfirmData")
    public Result<OrderConfirmVo> orderConfirmData() ;

    @GetMapping(value = "/api/inner/order/findOrderInfoById")
    public Result<OrderInfo> findOrderInfoById(@RequestParam(value = "orderId") Long orderId) ;

}
