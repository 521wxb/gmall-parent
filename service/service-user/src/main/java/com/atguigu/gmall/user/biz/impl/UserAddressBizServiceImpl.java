package com.atguigu.gmall.user.biz.impl;

import com.atguigu.gmall.cart.entity.UserAuthInfoVo;
import com.atguigu.gmall.common.util.UserAuthUtils;
import com.atguigu.gmall.user.biz.UserAddressBizService;
import com.atguigu.gmall.user.entity.UserAddress;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAddressBizServiceImpl implements UserAddressBizService {

    @Autowired
    private UserAddressMapper userAddressMapper ;

    @Override
    public List<UserAddress> findByUserId() {
        UserAuthInfoVo userAuthInfo = UserAuthUtils.getUserAuthInfo();
        LambdaQueryWrapper<UserAddress> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        lambdaQueryWrapper.eq(UserAddress::getUserId , userAuthInfo.getUserId()) ;
        List<UserAddress> userAddressList = userAddressMapper.selectList(lambdaQueryWrapper);
        return userAddressList;
    }
}
