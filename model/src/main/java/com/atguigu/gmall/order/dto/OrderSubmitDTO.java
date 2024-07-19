package com.atguigu.gmall.order.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class OrderSubmitDTO {

    /**
     * @NotNull: 表示的意思是该字段不能为null，但是不会对空字符串进行判断  ""
     * @NotBlank: 表示的意思是该字段不能为null，可以判断空字符串
     */
    @NotBlank(message = "收货人不能为空")
    private String consignee ;      // 收货人的名称

    @NotBlank(message = "收货人手机号码不能为空")
    @Length(min = 5 , max = 11 , message = "收货人手机号码长度不正确")
    private String consigneeTel ;    // 收货人手机号码

    @NotBlank(message = "收货人地址不能为空")
    private String deliveryAddress; // 收货地址

    private String orderComment ;    // 留言

    private String paymentWay = "online";  // 支付方式

    private List<DetailDTO> orderDetailList ;

}
