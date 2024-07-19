package com.atguigu.gmall.seckill.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.seckill.biz.SeckillBizService;
import com.atguigu.gmall.seckill.entity.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/inner/seckill")
public class SeckillRpcController {

    @Autowired
    private SeckillBizService seckillBizService ;

    @GetMapping(value = "/goods/list/today")
    public Result<List<SeckillGoods>> findAllToDaySeckillGoods() {
        List<SeckillGoods> seckillGoodsList = seckillBizService.findAllToDaySeckillGoods() ;
        return Result.build(seckillGoodsList , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/goods/{skuId}")
    public Result<SeckillGoods> findSeckillGoodsBySkuId(@PathVariable(value = "skuId") Long skuId) {
        SeckillGoods seckillGoods = seckillBizService.findSeckillGoodsBySkuId(skuId) ;
        return Result.build(seckillGoods , ResultCodeEnum.SUCCESS) ;
    }

}
