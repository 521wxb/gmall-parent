package com.atguigu.gmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "base_category1")
public class BaseCategory1 {

    @TableId(value = "id" , type = IdType.AUTO)
    private Long id ;

    @TableField(value = "name")
    private String name ;

}
