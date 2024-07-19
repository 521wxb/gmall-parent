package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.BloomFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 重置bloomFilter接口
 */
@RestController
@RequestMapping(value = "/admin/product")
public class BloomFilterController {

    @Autowired
    private BloomFilterService bloomFilterService ;

    @GetMapping(value = "/resetBloomFilter")
    public Result resetBloomFilter() {
        bloomFilterService.resetBloomFilter();
        return Result.ok() ;
    }

}
