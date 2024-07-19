package com.atguigu.gmall.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchRespAttrVo {

    private Long attrId ;           // 平台属性的id
    private String attrName;        // 平台属性的名称

    private List<String> attrValueList ;  // 平台属性的值

}
