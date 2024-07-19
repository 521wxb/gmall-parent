package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.item.service.AopService;
import com.atguigu.gmall.product.entity.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/aop")
public class AopController {

    @Autowired
    private AopService aopService ;

    @GetMapping(value = "/getSkuInfo/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable(value = "skuId") Long skuId) {
        SkuInfo skuInfo = aopService.getSkuInfo(skuId);
        return Result.build(skuInfo , ResultCodeEnum.SUCCESS) ;
    }

}
