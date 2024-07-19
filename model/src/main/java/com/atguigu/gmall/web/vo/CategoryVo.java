package com.atguigu.gmall.web.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVo {

    private Long categoryId ;                       // 分类的id
    private String categoryName  ;                  // 分类的名称
    private List<CategoryVo> categoryChild ;        // 封装的是该分类所对应的子分类

}
