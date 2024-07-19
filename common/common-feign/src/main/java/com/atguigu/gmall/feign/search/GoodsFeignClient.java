package com.atguigu.gmall.feign.search;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.search.fallbck.GoodsFeignClientFallback;
import com.atguigu.gmall.search.dto.SearchParamDTO;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "service-search" , fallback = GoodsFeignClientFallback.class)
public interface GoodsFeignClient {

    @PostMapping(value = "/api/inner/search/saveGoods")
    public Result saveGoods(@RequestBody Goods goods) ;


    @DeleteMapping(value = "/api/inner/search/deleteGoods/{skuId}")
    public Result deleteGoodsById(@PathVariable(value = "skuId") Long skuId) ;

    @PostMapping(value = "/api/inner/search/searchGoods")
    public Result<SearchResponseVo> search(@RequestBody SearchParamDTO searchParamDTO) ;

    @GetMapping(value = "/api/inner/search/updateHotScore")
    public Result updateHotScore(@RequestParam(value = "skuId") Long skuId , @RequestParam("hotScore") Long hotScore) ;

}
