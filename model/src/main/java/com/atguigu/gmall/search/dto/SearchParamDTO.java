package com.atguigu.gmall.search.dto;

import lombok.Data;

@Data
public class SearchParamDTO {

    //分类信息
    private Long category1Id;
    private Long category2Id;
    private Long category3Id;

    //关键字
    private String keyword;

    //品牌条件
    private String trademark;

    //属性条件 ["23:8G:运行内存","24:128G:机身内存"]
    private String[] props;

    //排序条件  1:desc
    private String order;

    //页码信息
    private Integer pageNo = 1;
    private Integer pageSize = 10;

}
