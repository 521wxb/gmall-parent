package com.atguigu.gmall.feign.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.fallback.SkuDetailFeignClientFallback;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.web.vo.AttrValueConcatVo;
import com.atguigu.gmall.web.vo.CategoryView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "service-product" , fallback = SkuDetailFeignClientFallback.class)
public interface SkuDetailFeignClient {

    @GetMapping(value = "/api/inner/product/category/{skuId}")
    public Result<CategoryView> findByCategoryBySkuId(@PathVariable(value = "skuId") Long skuId) ;

    @GetMapping(value = "/api/inner/product/skuInfo/{skuId}")
    public Result<SkuInfo> findBySkuInfoAndSkuImagesBySkuId(@PathVariable(value = "skuId") Long skuId) ;

    @GetMapping(value = "/api/inner/product/price/{skuId}")
    public Result<SkuInfo> findSkuInfoBySkuId(@PathVariable(value = "skuId") Long skuId) ;

    @GetMapping(value = "/api/inner/product/findSpuSalAttrBySkuId/{skuId}")
    public Result<List<SpuSaleAttr>> findSpuSalAttrBySkuId(@PathVariable(value = "skuId") Long skuId) ;

    @GetMapping(value = "/api/inner/product/findBrotherSkuSaleAttrValueConcatBySkuId/{skuId}")
    public Result<List<AttrValueConcatVo>> findBrotherSkuSaleAttrValueConcatBySkuId(@PathVariable(value = "skuId") Long skuId) ;

    @GetMapping(value = "/api/inner/product/skuInfo/skuIds")
    public Result<List<Long>> findAllSkuIds() ;
}
