package com.atguigu.gmall.search.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 封装每个SKU在es中要保存的数据
 */
@Data
@Document(indexName = "goods" , shards = 3 , replicas = 2)
public class Goods {
	
    // 商品Id skuId
    @Id
    private Long id; 

    // String 在 es中可以对应 Text（要分词） 、 Keyword（无需分词存）
    // index = false，无需索引。这个字段不会用来检索，不用建立索引
    @Field(type = FieldType.Keyword, index = false)
    private String defaultImg;

    //  es 中能分词的字段，这个字段数据类型必须是 text！keyword 不分词！
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String title; //skuName；

    @Field(type = FieldType.Double)
    private BigDecimal price;

    //  @Field(type = FieldType.Date)   6.8.1
    @Field(type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime; // 新品。 上架时间

	// 品牌id
    @Field(type = FieldType.Long)
    private Long tmId;

	// 品牌名称
    @Field(type = FieldType.Keyword)
    private String tmName;

	// 品牌log
    @Field(type = FieldType.Keyword)
    private String tmLogoUrl;
    //以上是当前sku的品牌信息

	// 三级分类的数据,用于进行检索(首页进入)
    @Field(type = FieldType.Long)
    private Long category1Id;

    @Field(type = FieldType.Keyword)
    private String category1Name;

    @Field(type = FieldType.Long)
    private Long category2Id;

    @Field(type = FieldType.Keyword)
    private String category2Name;

    @Field(type = FieldType.Long)
    private Long category3Id;

    @Field(type = FieldType.Keyword)
    private String category3Name;
	
    //详细分类信息

    //  商品的热度！ 我们将商品被用户点查看的次数越多，则说明热度就越高！
    @Field(type = FieldType.Long)
    private Long hotScore = 0L;

    // 平台属性集合对象
    // Nested 支持嵌套查询
    @Field(type = FieldType.Nested)
    private List<SearchAttr> attrs;

}
