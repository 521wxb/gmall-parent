package com.atguigu.gmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetailVo {

    private String imgUrl ;
    private String skuName ;
    private BigDecimal orderPrice ;
    private Integer skuNum ;
    private Long skuId ;            // 商品id
    private Integer hasStock ;      // 是否有货，如果返回的是1表示有货的

}
