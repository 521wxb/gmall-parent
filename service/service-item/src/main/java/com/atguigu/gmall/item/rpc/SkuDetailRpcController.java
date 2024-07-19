package com.atguigu.gmall.item.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.item.biz.SkuDetailBizService;
import com.atguigu.gmall.web.vo.SkuDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/inner/item")
public class SkuDetailRpcController {

    @Autowired
    private SkuDetailBizService skuDetailBizService ;

    @GetMapping(value = "/detail/{skuId}")
    public Result<SkuDetailVo> skuDetailBySkuId(@PathVariable(value = "skuId") Long skuId) {
        log.info("SkuDetailRpcController....skuDetailBySkuId...方法执行了，查询skuDetailVo的详情数据...start.........");
        SkuDetailVo skuDetailVo = skuDetailBizService.skuDetailBySkuId(skuId) ;
        log.info("SkuDetailRpcController....skuDetailBySkuId...方法执行了，查询skuDetailVo的详情数据...end.........{}" , skuDetailVo);
        skuDetailBizService.updateHotScore(skuId) ;  // 计算商品的热度分
        return Result.build(skuDetailVo , ResultCodeEnum.SUCCESS) ;
    }

}
