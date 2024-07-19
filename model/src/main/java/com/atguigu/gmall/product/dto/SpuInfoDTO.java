package com.atguigu.gmall.product.dto;

import com.atguigu.gmall.product.entity.SpuImage;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import lombok.Data;

import java.util.List;

@Data
public class SpuInfoDTO {

    private Long id ;                   // spu的id，在添加spu的时候次值是null
    private Long category3Id ;          // 三级分类的id
    private String description ;        // spu的描述
    private String spuName ;            // spu的名称
    private Long tmId ;                 // 品牌的id

    private List<SpuImage> spuImageList ;       // 接收图片数据

    private List<SpuSaleAttr> spuSaleAttrList ;            // 接收销售属性的名称
}
