package com.atguigu.gmall.feign.item;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.item.fallback.SkuDetailFeignClientFallback;
import com.atguigu.gmall.web.vo.SkuDetailVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-item" , fallback = SkuDetailFeignClientFallback.class)
public interface SkuDetailFeignClient {

    @GetMapping(value = "/api/inner/item/detail/{skuId}")
    public Result<SkuDetailVo> skuDetailBySkuId(@PathVariable(value = "skuId") Long skuId) ;

}
