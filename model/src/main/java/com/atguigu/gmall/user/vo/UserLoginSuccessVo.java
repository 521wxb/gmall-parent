package com.atguigu.gmall.user.vo;

import lombok.Data;

@Data
public class UserLoginSuccessVo {

    private String token ;      // 登录成功以后，后端给前端返回的token
    private String nickName ;   // 登录用户的别名

}
