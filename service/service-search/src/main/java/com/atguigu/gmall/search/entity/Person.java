package com.atguigu.gmall.search.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "person" , shards = 1 , replicas = 1)
@Data
public class Person {

    @Id
    private Long id;

    /**
     * Text和Keyword都可以表示字符串
     * 区别：Text可以对数据进行分词，Keyword不能进行分词
     */
    @Field(type = FieldType.Text , name = "username" , analyzer = "ik_smart")
    private String username;

    @Field(type = FieldType.Text , name = "address" , analyzer = "ik_smart")
    private String address;

    @Field(type = FieldType.Integer , name = "age")
    private Integer age;

}
