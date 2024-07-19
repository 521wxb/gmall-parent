package com.atguigu.gmall.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.user.entity.UserAddress;
import com.atguigu.gmall.user.service.UserAddressService;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user_address(用户地址表)】的数据库操作Service实现
* @createDate 2023-02-22 10:56:52
*/
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress>
    implements UserAddressService{

}




