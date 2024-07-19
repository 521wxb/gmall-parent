package com.atguigu.gmall.ware.entity;

import lombok.Data;

import java.util.List;

@Data
public class WareStockMsg {

   private Long orderId ;               // 定义的id
   private String consignee ;           // 收件人
   private String consigneeTel;         // 收件人手机号码
   private String orderComment ;        // 备注
   private String orderBody ;           // 订单摘要
   private String deliveryAddress ;     // 收货人地址
   private String paymentWay ;          // 支付方式
   private Long wareId ;                // 仓库的id

   private List<Sku> details ;          // 定义明细

}
