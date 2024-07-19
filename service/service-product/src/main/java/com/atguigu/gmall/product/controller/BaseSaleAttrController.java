package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.entity.BaseSaleAttr;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/product/")
public class BaseSaleAttrController {

    @Autowired
    private BaseSaleAttrService baseSaleAttrService ;

    @GetMapping(value = "/baseSaleAttrList")
    public Result<List<BaseSaleAttr>> findAll() {
        List<BaseSaleAttr> saleAttrList = baseSaleAttrService.list();
        return Result.build(saleAttrList , ResultCodeEnum.SUCCESS) ;
    }

}
