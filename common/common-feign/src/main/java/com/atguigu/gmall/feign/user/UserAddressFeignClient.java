package com.atguigu.gmall.feign.user;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.user.fallback.UserAddressFeignClientFallback;
import com.atguigu.gmall.user.entity.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "service-user" , fallback = UserAddressFeignClientFallback.class)
public interface UserAddressFeignClient {

    @GetMapping(value = "/api/inner/user/address/getUserAllAddress")
    public Result<List<UserAddress>> findByUserId() ;

}
