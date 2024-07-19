package com.atguigu.gmall.order.service;

import com.atguigu.gmall.order.dto.OrderSubmitDTO;
import com.atguigu.gmall.order.entity.OrderInfo;
import com.atguigu.gmall.ware.entity.WareStockMsg;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【order_info(订单表 订单表)】的数据库操作Service
* @createDate 2023-02-25 14:20:14
*/
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 保存订单数据
     * @param orderSubmitDTO
     * @param tradeNo
     * @return
     */
    public abstract String submitOrder(OrderSubmitDTO orderSubmitDTO, String tradeNo);

    /**
     * 关闭订单操作
     * @param userId
     * @param orderId
     */
    public abstract void closeOrder(Long userId, Long orderId);

    /**
     * 更改订单的状态为
     * @param messageBody
     */
    public abstract void orderPayedStatus(String messageBody);

    /**
     * 库存扣减成功以后更改订单的状态
     * @param msg
     */
    public abstract void updateOrderStatusWare(String msg);

    /**
     * 进行拆单操作
     * @param orderId
     * @param wareSkuMap
     * @return
     */
    public abstract List<WareStockMsg> orderSplit(Long orderId, String wareSkuMap);

    /**
     * 查询当前用户的订单数据
     * @param pageNo
     * @param pageSize
     * @return
     */
    public abstract Page findOrderListByUserId(Integer pageNo, Integer pageSize);

}
