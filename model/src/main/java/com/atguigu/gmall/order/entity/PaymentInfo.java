package com.atguigu.gmall.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 支付信息表
 * @TableName payment_info
 */
@TableName(value ="payment_info")
@Data
public class PaymentInfo implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId ;

    /**
     * 对外业务编号
     */
    private String outTradeNo;

    /**
     * 订单编号
     */
    private String orderId;

    /**
     * 支付类型（微信 支付宝）
     */
    private String paymentType;

    /**
     * 交易编号
     */
    private String tradeNo;

    /**
     * 支付金额
     */
    private BigDecimal totalAmount;

    /**
     * 交易内容
     */
    private String subject;

    /**
     * 支付状态
     */
    private String paymentStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 回调时间
     */
    private Date callbackTime;

    /**
     * 回调信息
     */
    private String callbackContent;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
