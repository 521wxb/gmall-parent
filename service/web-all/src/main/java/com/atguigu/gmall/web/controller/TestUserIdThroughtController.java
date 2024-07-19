package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试用户id是否可以传递到后端微服务中，
 * 从服务网关中把用户的id传递到后端微服务中，这个功能被称之为用户id透传！
 */
@RestController
@RequestMapping(value = "/cart")
public class TestUserIdThroughtController {

    @GetMapping(value = "/haha")
    public Result userIdThrought(@RequestHeader(name = "userId" , required = false) String userId,
                                 @RequestHeader(name = "userTempId" , required = false) String userTempId) {
        System.out.println(userId);
        System.out.println(userTempId);
        return Result.ok() ;
    }

}
