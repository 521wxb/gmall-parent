package com.atguigu.gmall.feign.item.fallback;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.item.SkuDetailFeignClient;
import com.atguigu.gmall.web.vo.SkuDetailVo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Component
public class SkuDetailFeignClientFallback implements SkuDetailFeignClient {

    @Override
    public Result<SkuDetailVo> skuDetailBySkuId(Long skuId) {
        log.error("SkuDetailFeignClientFallback....skuDetailBySkuId的fallback方法执行了");
        return Result.ok();
    }

}
