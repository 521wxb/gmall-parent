package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.item.SkuDetailFeignClient;
import com.atguigu.gmall.web.vo.SkuDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class ItemController {

    @Autowired
    private SkuDetailFeignClient skuDetailFeignClient ;

    @GetMapping(value = "/{skuId}.html")
    public String item(@PathVariable(value = "skuId") Long skuId , Model model) {

        // 远程调用service-item接口获取数据
        Result<SkuDetailVo> skuDetailVoResult = skuDetailFeignClient.skuDetailBySkuId(skuId) ;
        SkuDetailVo skuDetailVo = skuDetailVoResult.getData();
        log.info("ItemController...item...方法执行了，远程调用了service-item的查询sku详情的接口，得到的数据为：{}" , skuDetailVo);
        if(skuDetailVo == null) {
            return "item/error" ;
        }

        // 把数据存储到model对象中,三级分类数据
        model.addAttribute("categoryView" , skuDetailVo.getCategoryView()) ;

        // 把数据存储到model对象中,skuInfo
        model.addAttribute("skuInfo" , skuDetailVo.getSkuInfo()) ;

        // 把数据存储到model对象中,price
        model.addAttribute("price" , skuDetailVo.getPrice()) ;

        // 把数据存储到model对象中,spu的销售属性和销售属性值
        model.addAttribute("spuSaleAttrList" , skuDetailVo.getSpuSaleAttrList()) ;

        // 把数据存储到model对象中,当前sku所对应的=兄弟的sku的销售属性值的组合
        model.addAttribute("valuesSkuJson" , skuDetailVo.getValuesSkuJson()) ;

        // 返回页面的路径
        return "item/index" ;

    }

}
