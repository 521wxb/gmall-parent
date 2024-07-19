package com.atguigu.gmall.feign.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.fallback.CategoryFeignClientFallBack;
import com.atguigu.gmall.web.vo.CategoryVo;
import feign.Request;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "service-product" , fallback = CategoryFeignClientFallBack.class)
public interface CategoryFeignClient {

    @GetMapping(value = "/api/inner/product/category/trees")
    public Result<List<CategoryVo>> findAllCategory() ;

}

