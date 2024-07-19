package com.atguigu.gmall.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetailDTO {

    private String imgUrl ;
    private String skuName ;
    private BigDecimal orderPrice ;
    private Long skuId ;
    private Integer skuNum ;

}
