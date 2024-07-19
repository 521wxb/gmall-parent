package com.atguigu.gmall.search.vo;

import com.atguigu.gmall.search.dto.SearchParamDTO;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.entity.SearchAttr;
import lombok.Data;

import java.util.List;

@Data
public class SearchResponseVo {

    // 请求参数封装，需要将其返回给前端
    private SearchParamDTO searchParam ;

    // 品牌的面包屑
    private String trademarkParam ;

    // 需要urlParam
    private String urlParam ;

    // 平台属性面包屑
    private List<SearchAttr> propsParamList ;

    // 品牌的列表
    private List<SearchTmVo> trademarkList ;

    // 平台属性的列表
    private List<SearchRespAttrVo> attrsList ;

    // 排序信息
    private SearchOrderMapVo orderMap ;

    // 封装搜索的商品数据
    private List<Goods> goodsList ;

    // 分页相关参数
    private Integer pageNo ;
    private Integer totalPages ;

}
