package com.atguigu.gmall.common.result;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 *
 */
@Getter
public enum ResultCodeEnum {

    USER_LOGIN_ERROR(50001 , "用户名或者密码错误！"),


    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务异常"),

    REF_SPU_ERROR(19000 , "当前要删除的品牌数据被spu所进行关联") ,
    REF_SKU_ERROR(19001 , "当前要删除的品牌数据被sku所进行关联") ,

    SYSTEM_EXCEPTION(9999 , "你的网络有问题，请稍后重试！") ,

    REDIS_SKU_DETAIL_ERROR(102033 , "你查询的数据不存在，请通过正常途径进行访问!") ,

    PAY_RUN(205, "支付中"),

    LOGIN_AUTH(208, "未登陆"),
    PERMISSION(209, "没有权限"),


    SECKILL_NO_START(210, "秒杀还没开始"),
    SECKILL_RUN(211, "正在排队中"),
    SECKILL_NO_PAY_ORDER(212, "您有未支付的订单"),
    SECKILL_FINISH(213, "已售罄"),
    SECKILL_END(214, "秒杀已结束"),
    SECKILL_SUCCESS(215, "抢单成功"),
    SECKILL_FAIL(216, "抢单失败"),
    SECKILL_ILLEGAL(217, "请求不合法"),
    SECKILL_ORDER_SUCCESS(218, "下单成功"),
    COUPON_GET(220, "优惠券已经领取"),
    COUPON_LIMIT_GET(221, "优惠券已发放完毕"),



    // 购物车模块所需要的枚举项
    SKUITEM_CATGRAY_COUNT(60100 , "购物项的数量超过限定，限定数量为3个"),
    VALID_ERROR(60101 , "数据不合法"),
    REQ_REPEAT(70100 , "表达重复提交了,请重新刷新") ,
    SKU_PRICE_CHANGE(80100 , "价格发生了改变，请重新刷新获取最新价格数据!"),
    SKU_NOT_HAS_STOCK(90100 , "存在没有库存的商品，请重新选择！")

    ;

    private Integer code;

    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
