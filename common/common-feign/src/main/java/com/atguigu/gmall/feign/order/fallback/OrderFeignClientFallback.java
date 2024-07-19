package com.atguigu.gmall.feign.order.fallback;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderFeignClientFallback implements OrderFeignClient {

    @Override
    public Result<OrderConfirmVo> orderConfirmData() {
        log.error("OrderFeignClientFallback....orderConfirmData的fallback方法执行了");
        return Result.ok();
    }

    @Override
    public Result<OrderInfo> findOrderInfoById(Long orderId) {
        log.error("OrderFeignClientFallback....findOrderInfoById的fallback方法执行了");
        return Result.ok();
    }

}
