package com.atguigu.gmall.feign.product.fallback;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.CategoryFeignClient;
import com.atguigu.gmall.web.vo.CategoryVo;
import feign.Request;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
// @Component
public class CategoryFeignClientFallBack implements CategoryFeignClient {

    @Override
    public Result<List<CategoryVo>> findAllCategory() {     // 兜底方案
        log.error("CategoryFeignClientFallBack....findAllCategory兜底方法执行了....");
        return new Result<>();
    }

}
