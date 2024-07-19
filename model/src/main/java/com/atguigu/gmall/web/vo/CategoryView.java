package com.atguigu.gmall.web.vo;

import lombok.Data;

@Data
public class CategoryView {

    private Long category1Id ;          // 一级分类的id
    private String category1Name ;      // 一级分类的名称
    private Long category2Id ;          // 二级分类的id
    private String category2Name ;      // 二级分类的名称

    private Long category3Id ;          // 三级分类的id
    private String category3Name ;      // 三级分类的名称

}
