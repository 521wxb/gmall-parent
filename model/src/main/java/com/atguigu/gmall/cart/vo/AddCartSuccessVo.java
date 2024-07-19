package com.atguigu.gmall.cart.vo;

import com.atguigu.gmall.product.entity.SkuInfo;
import lombok.Data;

@Data
public class AddCartSuccessVo {

    private SkuInfo skuInfo ;
    private Integer skuNum ;

}
