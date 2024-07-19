package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.order.dto.OrderSubmitDTO;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.ware.entity.WareStockMsg;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/order")
public class OrderController {

    @Autowired
    private OrderInfoService orderInfoService ;

    /**
     * @Valid:针对这个Java Bean就需要开启数据校验的功能
     * @param orderSubmitDTO
     * @param tradeNo
     * @return
     */
    @PostMapping(value = "/auth/submitOrder")
    public Result submitOrder(@Valid @RequestBody OrderSubmitDTO orderSubmitDTO , @RequestParam(name = "tradeNo") String tradeNo) {
        String orderId = orderInfoService.submitOrder(orderSubmitDTO , tradeNo) ;
        return Result.build(orderId , ResultCodeEnum.SUCCESS) ;
    }

    @PostMapping(value = "/orderSplit")
    public List<WareStockMsg> orderSplit(@RequestParam(value = "orderId") Long orderId , @RequestParam("wareSkuMap") String wareSkuMap) {
        List<WareStockMsg> wareStockMsgList = orderInfoService.orderSplit(orderId , wareSkuMap) ;
        return wareStockMsgList ;
    }

    @GetMapping(value = "/auth/{page}/{limit}")
    public Result<Page> findOrderListByUserId(@PathVariable(value = "page") Integer pageNo , @PathVariable(value = "limit") Integer pageSize) {
        Page page = orderInfoService.findOrderListByUserId(pageNo , pageSize) ;
        return Result.build(page , ResultCodeEnum.SUCCESS) ;
    }

}
