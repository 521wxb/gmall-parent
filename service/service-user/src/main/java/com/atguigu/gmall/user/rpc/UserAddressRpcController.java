package com.atguigu.gmall.user.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.user.biz.UserAddressBizService;
import com.atguigu.gmall.user.entity.UserAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/inner/user")
public class UserAddressRpcController {

    @Autowired
    private UserAddressBizService userAddressBizService ;

    @GetMapping(value = "/address/getUserAllAddress")
    public Result<List<UserAddress>> findByUserId() {
        List<UserAddress> userAddressList = userAddressBizService.findByUserId() ;
        return Result.build(userAddressList , ResultCodeEnum.SUCCESS) ;
    }

}
