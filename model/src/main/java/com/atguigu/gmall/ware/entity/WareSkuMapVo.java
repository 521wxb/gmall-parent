package com.atguigu.gmall.ware.entity;

import lombok.Data;

import java.util.List;

@Data
public class WareSkuMapVo {

    private Long wareId ;
    private List<Long> skuIds ;

}
