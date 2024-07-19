package com.atguigu.gmall.feign.search.fallbck;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.search.GoodsFeignClient;
import com.atguigu.gmall.search.dto.SearchParamDTO;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Component
public class GoodsFeignClientFallback implements GoodsFeignClient {

    @Override
    public Result saveGoods(Goods goods) {
        log.error("GoodsFeignClientFallback....saveGoods...降级方法执行了...");
        return Result.ok();
    }

    @Override
    public Result deleteGoodsById(Long skuId) {
        log.error("GoodsFeignClientFallback....deleteGoodsById...降级方法执行了...");
        return Result.ok();
    }

    @Override
    public Result<SearchResponseVo> search(SearchParamDTO searchParamDTO) {
        log.error("GoodsFeignClientFallback....search...降级方法执行了...");
        return Result.ok();
    }

    @Override
    public Result updateHotScore(Long skuId, Long hotScore) {
        log.error("GoodsFeignClientFallback....updateHotScore..降级方法执行了...");
        return Result.ok();
    }

}
