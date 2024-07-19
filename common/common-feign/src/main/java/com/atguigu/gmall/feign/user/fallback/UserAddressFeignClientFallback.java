package com.atguigu.gmall.feign.user.fallback;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.user.UserAddressFeignClient;
import com.atguigu.gmall.user.entity.UserAddress;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class UserAddressFeignClientFallback implements UserAddressFeignClient {

    @Override
    public Result<List<UserAddress>> findByUserId() {
        log.error("UserAddressFeignClientFallback....findByUserId的fallback方法执行了");
        return Result.ok();
    }

}
