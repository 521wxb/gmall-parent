package com.atguigu.gmall.feign.seckill.fallback;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.seckill.SeckillFeignClient;
import com.atguigu.gmall.seckill.entity.SeckillGoods;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SeckillFeignClientFallback implements SeckillFeignClient {

    @Override
    public Result<List<SeckillGoods>> findAllToDaySeckillGoods() {
        log.error("SeckillFeignClientFallback....findAllToDaySeckillGoods...降级方法执行了...");
        return Result.ok();
    }

    @Override
    public Result<SeckillGoods> findSeckillGoodsBySkuId(Long skuId) {
        log.error("SeckillFeignClientFallback....findSeckillGoodsBySkuId...降级方法执行了...");
        return Result.ok();
    }

}
