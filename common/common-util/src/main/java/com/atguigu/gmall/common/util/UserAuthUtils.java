package com.atguigu.gmall.common.util;

import com.atguigu.gmall.cart.entity.UserAuthInfoVo;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class UserAuthUtils {

    public static UserAuthInfoVo getUserAuthInfo() {

        // 获取httpServletRequest对象，以及用户的id和临时用户的id
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = requestAttributes.getRequest();
        String userId = httpServletRequest.getHeader("userId");
        String userTempId = httpServletRequest.getHeader("userTempId");

        // 封装数据到UserAuthInfoVo对象中
        UserAuthInfoVo userAuthInfoVo = new UserAuthInfoVo() ;
        if(!StringUtils.isEmpty(userId)) {
            userAuthInfoVo.setUserId(Long.parseLong(userId));
        }
        userAuthInfoVo.setUserTempId(userTempId);

        // 返回
        return userAuthInfoVo ;
    }

}
