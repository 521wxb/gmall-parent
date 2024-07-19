package com.atguigu.gmall.web.vo;

import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import lombok.Data;

import java.util.List;

@Data
public class SkuDetailVo {

    private CategoryView categoryView ;                     // 封装3级分类的数据
    private SkuInfo skuInfo ;                               // 封装的sku的基本信息以及这个sku所对应的图片
    private double price ;                                  // sku的价格
    private List<SpuSaleAttr> spuSaleAttrList ;             // sku所对应的spu的销售属性以及销售属性值

    private String valuesSkuJson ;   // TODO，后面解释

}
