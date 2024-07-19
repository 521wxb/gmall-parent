package com.atguigu.gmall.feign.cart.fallback;

import com.atguigu.gmall.cart.entity.CartItem;
import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CartFeignClientFallback implements CartFeignClient {

    @Override
    public Result<AddCartSuccessVo> addCart(Long skuId, Integer skuNum) {
        log.error("CartFeignClientFallback....addCart的fallback方法执行了");
        return Result.ok();
    }

    @Override
    public Result deleteCheckedCart() {
        log.error("CartFeignClientFallback....deleteCheckedCart的fallback方法执行了");
        return Result.ok();
    }

    @Override
    public Result<List<CartItem>> getCheckedCartItem() {
        log.error("CartFeignClientFallback....getCheckedCartItem的fallback方法执行了");
        return Result.ok();
    }

}
