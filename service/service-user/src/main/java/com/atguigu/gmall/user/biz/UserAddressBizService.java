package com.atguigu.gmall.user.biz;

import com.atguigu.gmall.user.entity.UserAddress;

import java.util.List;

public interface UserAddressBizService {

    /**
     * 根据用户的id获取用户的收货地址列表
     * @return
     */
    public abstract List<UserAddress> findByUserId();

}
