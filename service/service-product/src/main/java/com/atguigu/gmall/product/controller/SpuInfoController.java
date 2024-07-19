package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.dto.SpuInfoDTO;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping(value = "/admin/product")
public class SpuInfoController {

    @Autowired
    private SpuInfoService spuInfoService ;

    @GetMapping(value = "/{page}/{limit}")
    public Result<Page> findByPage(@PathVariable(value = "page") Integer pageNo ,
                                   @PathVariable(value = "limit") Integer pageSize ,
                                   @RequestParam("category3Id") Long category3Id) {
        Page page = spuInfoService.findByPage(pageNo , pageSize , category3Id) ;
        return Result.build(page , ResultCodeEnum.SUCCESS) ;
    }

    @PostMapping(value = "/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfoDTO spuInfoDTO) {
        spuInfoService.saveSpuInfo(spuInfoDTO) ;
        return Result.build(null , ResultCodeEnum.SUCCESS) ;
    }

}
