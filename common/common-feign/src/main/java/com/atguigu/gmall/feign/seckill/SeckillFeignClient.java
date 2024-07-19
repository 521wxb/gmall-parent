package com.atguigu.gmall.feign.seckill;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.seckill.fallback.SeckillFeignClientFallback;
import com.atguigu.gmall.seckill.entity.SeckillGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "service-seckill" , fallback = SeckillFeignClientFallback.class)
public interface SeckillFeignClient {

    @GetMapping(value = "/api/inner/seckill/goods/list/today")
    public Result<List<SeckillGoods>> findAllToDaySeckillGoods() ;

    @GetMapping(value = "/api/inner/seckill/goods/{skuId}")
    public Result<SeckillGoods> findSeckillGoodsBySkuId(@PathVariable(value = "skuId") Long skuId) ;

}
