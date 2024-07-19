package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.entity.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/product/")
public class BaseAttrInfoController {

    @Autowired
    private BaseAttrInfoService baseAttrInfoService ;

    @GetMapping(value = "/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result<List<BaseAttrInfo>> findByCategoryId(@PathVariable(value = "category1Id") Long c1Id ,
                                                       @PathVariable(value = "category2Id") Long c2Id ,
                                                       @PathVariable(value = "category3Id") Long c3Id) {
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoService.findByCategoryId(c1Id , c2Id , c3Id);
        return Result.build(baseAttrInfoList , ResultCodeEnum.SUCCESS) ;
    }

    @PostMapping(value = "/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo ) {
        baseAttrInfoService.saveAttrInfo(baseAttrInfo);
        return Result.ok() ;
    }

}
