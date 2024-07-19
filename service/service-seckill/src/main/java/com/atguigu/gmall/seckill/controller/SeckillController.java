package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/activity/seckill")
public class SeckillController {

    @Autowired
    private SeckillGoodsService seckillGoodsService ;

    @GetMapping(value = "/auth/getSeckillSkuIdStr/{skuId}")
    public Result<String> genSeckillCode(@PathVariable(value = "skuId") Long skuId) {
        String seckillCode = seckillGoodsService.genSeckillCode(skuId) ;
        return Result.build(seckillCode , ResultCodeEnum.SUCCESS) ;
    }

    @PostMapping(value="/auth/seckillOrder/{skuId}")
    public Result queue(@PathVariable(value = "skuId") Long skuId , @RequestParam(value = "skuIdStr") String skuIdStr) {
        seckillGoodsService.queue(skuId , skuIdStr) ;
        return Result.ok() ;
    }

    @GetMapping(value = "/auth/checkOrder/{skuId}")
    public Result checkOrderStatus(@PathVariable(value = "skuId") Long skuId) {
        ResultCodeEnum resultCodeEnum = seckillGoodsService.checkOrderStatus(skuId) ;
        return Result.build("" , resultCodeEnum) ;
    }

}
