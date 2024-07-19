package com.atguigu.gmall.order.biz;

import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.order.vo.OrderConfirmVo;

public interface OrderBizService {

    /**
     * 获取用户购物车中选中商品数据
     * @return
     */
    public abstract OrderConfirmVo orderConfirmData();

    /**
     * 根据订单的id查询订单数据
     * @param orderId
     * @return
     */
    public abstract OrderInfo findOrderInfoById(Long orderId);
}
