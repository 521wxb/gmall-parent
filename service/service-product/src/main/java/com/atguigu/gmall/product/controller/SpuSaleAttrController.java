package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/product")
public class SpuSaleAttrController {

    @Autowired
    private SpuSaleAttrService spuSaleAttrService ;

    @GetMapping(value = "/spuSaleAttrList/{spuId}")
    public Result<List<SpuSaleAttr>> findBySpuId(@PathVariable(value = "spuId") Long spuId) {
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrService.findBySpuId(spuId) ;
        return Result.build(spuSaleAttrs , ResultCodeEnum.SUCCESS) ;
    }

}
