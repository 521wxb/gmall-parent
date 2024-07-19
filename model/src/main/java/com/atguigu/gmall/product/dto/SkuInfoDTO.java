package com.atguigu.gmall.product.dto;

import com.atguigu.gmall.product.entity.SkuAttrValue;
import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.entity.SkuSaleAttrValue;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuInfoDTO {

    private Long category3Id ;      // 三级分类的id
    private Long id ;               // skuInfo的id，前端不会传递
    private Integer price ;         // 价格
    private String skuDefaultImg ;  // 默认的图片的url
    private String skuDesc ;        // 描述信息
    private String  skuName ;       // sku的名称
    private Long spuId ;            // spuId
    private Long tmId ;             // 品牌的id
    private BigDecimal weight ;     // 重量

    private List<SkuAttrValue> skuAttrValueList ;        // 平台属性值的集合
    private List<SkuImage> skuImageList ;                // sku的图片数据
    private List<SkuSaleAttrValue> skuSaleAttrValueList ; // sku销售属性值的集合

}
