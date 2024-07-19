package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.entity.SpuImage;
import com.atguigu.gmall.product.service.SpuImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/product")
public class SpuImageController {

    @Autowired
    private SpuImageService spuImageService ;

    @GetMapping(value = "/spuImageList/{spuId}")
    public Result<List<SpuImage>> findBySpuId(@PathVariable(value = "spuId") Long spuId) {
        List<SpuImage> imageList = spuImageService.findBySpuId(spuId) ;
        return Result.build(imageList , ResultCodeEnum.SUCCESS) ;
    }

}
