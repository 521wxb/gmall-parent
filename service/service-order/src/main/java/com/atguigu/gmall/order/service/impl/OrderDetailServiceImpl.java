package com.atguigu.gmall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.order.entity.OrderDetail;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2023-02-25 14:20:14
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService{

}




