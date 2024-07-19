package com.atguigu.gmall.feign.product.fallback;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.SkuDetailFeignClient;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.web.vo.AttrValueConcatVo;
import com.atguigu.gmall.web.vo.CategoryView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
//@Component
public class SkuDetailFeignClientFallback implements SkuDetailFeignClient {

    @Override
    public Result<CategoryView> findByCategoryBySkuId(Long skuId) {
        log.error("SkuDetailFeignClientFallback.....findByCategoryBySkuId服务降级方法执行了...");
        return Result.ok();
    }

    @Override
    public Result<SkuInfo> findBySkuInfoAndSkuImagesBySkuId(Long skuId) {
        log.error("SkuDetailFeignClientFallback.....findBySkuInfoAndSkuImagesBySkuId服务降级方法执行了...");
        return Result.ok();
    }

    @Override
    public Result<SkuInfo> findSkuInfoBySkuId(Long skuId) {
        log.error("SkuDetailFeignClientFallback.....findSkuInfoBySkuId服务降级方法执行了...");
        return Result.ok();
    }

    @Override
    public Result<List<SpuSaleAttr>> findSpuSalAttrBySkuId(Long skuId) {
        log.error("SkuDetailFeignClientFallback.....findSpuSalAttrBySkuId服务降级方法执行了...");
        return Result.ok();
    }

    @Override
    public Result<List<AttrValueConcatVo>> findBrotherSkuSaleAttrValueConcatBySkuId(Long skuId) {
        log.error("SkuDetailFeignClientFallback.....findBrotherSkuSaleAttrValueConcatBySkuId服务降级方法执行了...");
        return Result.ok();
    }

    @Override
    public Result<List<Long>> findAllSkuIds() {
        log.error("SkuDetailFeignClientFallback.....findAllSkuIds服务降级方法执行了...");
        return Result.ok();
    }
}
