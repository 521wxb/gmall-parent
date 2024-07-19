package com.atguigu.gmall.product.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.biz.SkuDetailBizService;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.web.vo.AttrValueConcatVo;
import com.atguigu.gmall.web.vo.CategoryView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/inner/product")   // 所有的内部业务接口的请求路径的规则：/api/inner/模块名称
public class SkuDetailRpcController {

    @Autowired
    private SkuDetailBizService skuDetailBizService ;

    @GetMapping(value = "/category/{skuId}")
    public Result<CategoryView> findByCategoryBySkuId(@PathVariable(value = "skuId") Long skuId) {
        CategoryView categoryView = skuDetailBizService.findByCategoryBySkuId(skuId) ;
        return Result.build(categoryView , ResultCodeEnum.SUCCESS);
    }

    @GetMapping(value = "/skuInfo/{skuId}")
    public Result<SkuInfo> findBySkuInfoAndSkuImagesBySkuId(@PathVariable(value = "skuId") Long skuId) {
        SkuInfo skuInfo = skuDetailBizService.findBySkuInfoAndSkuImagesBySkuId(skuId) ;
        return Result.build(skuInfo , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/price/{skuId}")
    public Result<SkuInfo> findSkuInfoBySkuId(@PathVariable(value = "skuId") Long skuId) {
        SkuInfo skuInfo = skuDetailBizService.findSkuInfoBySkuId(skuId) ;
        return Result.build(skuInfo , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/findSpuSalAttrBySkuId/{skuId}")
    public Result<List<SpuSaleAttr>> findSpuSalAttrBySkuId(@PathVariable(value = "skuId") Long skuId) {
        List<SpuSaleAttr> spuSaleAttrList = skuDetailBizService.findSpuSalAttrBySkuId(skuId) ;
        return Result.build(spuSaleAttrList , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/findBrotherSkuSaleAttrValueConcatBySkuId/{skuId}")
    public Result<List<AttrValueConcatVo>> findBrotherSkuSaleAttrValueConcatBySkuId(@PathVariable(value = "skuId") Long skuId) {
        List<AttrValueConcatVo> attrValueConcatVoList = skuDetailBizService.findBrotherSkuSaleAttrValueConcatBySkuId(skuId) ;
        return Result.build(attrValueConcatVoList , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/skuInfo/skuIds")
    public Result<List<Long>> findAllSkuIds() {
        List<Long> skuIds = skuDetailBizService.findAllSkuIds() ;
        return Result.build(skuIds , ResultCodeEnum.SUCCESS) ;
    }


}
