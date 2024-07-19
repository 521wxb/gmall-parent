package com.atguigu.gmall.cart.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 购物车中的一个商品信息
 */
@Data
@ApiModel(description = "购物车中一个商品")
public class CartItem {
    private static final long serialVersionUID = 1L;

    private Long id; //自增id

    @ApiModelProperty(value = "skuid")
    private Long skuId; //商品id

    //第一次这个商品放入购物车时的价格会保存起来。
    @ApiModelProperty(value = "放入购物车时价格")
    private BigDecimal cartPrice; //100

    //如果后台改了商品价格，实时价格发生变化，页面展示实时价格 skuInfo.price
    //【页面提示】：购物车中有降价
    private BigDecimal skuPrice;   // 99

    @ApiModelProperty(value = "数量")
    private Integer skuNum;

    @ApiModelProperty(value = "图片文件")
    private String imgUrl;

    @ApiModelProperty(value = "sku名称 (冗余)")
    private String skuName;

    @ApiModelProperty(value = "isChecked")
    private Integer isChecked = 1;

    private Date createTime;
    private Date updateTime;

}
