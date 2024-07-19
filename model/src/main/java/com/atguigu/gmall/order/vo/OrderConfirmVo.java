package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.user.entity.UserAddress;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderConfirmVo {

    private List<DetailVo> detailArrayList ;        // 封装订单的详情数据

    private List<UserAddress> userAddressList ;           // 用户的收货地址列表

    private Integer totalNum ;                          // 购物的商品的总数量

    private BigDecimal totalAmount ;                    // 购买的商品的总金额

    private String tradeNo ;                            // 外部交易号
}
