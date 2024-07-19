package com.atguigu.gmall.user.service;

import com.atguigu.gmall.user.dto.UserLoginDTO;
import com.atguigu.gmall.user.entity.UserInfo;
import com.atguigu.gmall.user.vo.UserLoginSuccessVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【user_info(用户表)】的数据库操作Service
* @createDate 2023-02-22 10:56:53
*/
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 登录的业务方法
     * @param userLoginDTO
     * @return
     */
    public abstract UserLoginSuccessVo login(UserLoginDTO userLoginDTO);

    /**
     * 退出
     * @param token
     */
    public abstract void logout(String token);

}
