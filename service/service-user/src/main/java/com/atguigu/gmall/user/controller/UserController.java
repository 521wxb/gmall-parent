package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.user.dto.UserLoginDTO;
import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.vo.UserLoginSuccessVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(value = "/api/user")
@Slf4j
public class UserController {

    @Autowired
    private UserInfoService userInfoService ;

    @PostMapping(value = "/passport/login")
    public Result login(@RequestBody UserLoginDTO userLoginDTO ) {
        UserLoginSuccessVo userLoginSuccessVo = userInfoService.login(userLoginDTO) ;
        return Result.build(userLoginSuccessVo , ResultCodeEnum.SUCCESS) ;
    }

    /**
     * @RequestHeader获取指定请求头中的数据
     *
     * @param token
     * @return
     */
    @GetMapping(value = "/passport/logout")
    public Result logout(@RequestHeader(name = "token") String token) {
        userInfoService.logout(token) ;
        log.info("当前登录用户的token数据：" + token);
        return Result.ok() ;
    }

}
