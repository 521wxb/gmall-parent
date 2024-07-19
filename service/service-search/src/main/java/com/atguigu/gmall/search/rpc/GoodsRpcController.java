package com.atguigu.gmall.search.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.search.biz.GoodsBizService;
import com.atguigu.gmall.search.dto.SearchParamDTO;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/inner/search")
public class GoodsRpcController {

    @Autowired
    private GoodsBizService goodsBizService ;

    @PostMapping(value = "/saveGoods")
    public Result saveGoods(@RequestBody Goods goods) {
        goodsBizService.saveGoods(goods) ;
        return Result.ok() ;
    }

    @DeleteMapping(value = "/deleteGoods/{skuId}")
    public Result deleteGoodsById(@PathVariable(value = "skuId") Long skuId) {
        goodsBizService.deleteGoodsById(skuId) ;
        return Result.ok() ;
    }

    @PostMapping(value = "/searchGoods")
    public Result<SearchResponseVo> search(@RequestBody SearchParamDTO searchParamDTO) {
        SearchResponseVo searchResponseVo = goodsBizService.search(searchParamDTO) ;
        return Result.build(searchResponseVo , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/updateHotScore")
    public Result updateHotScore(@RequestParam(value = "skuId") Long skuId , @RequestParam("hotScore") Long hotScore) {
        goodsBizService.updateHotScore(skuId , hotScore) ;
        return Result.ok() ;
    }

}
