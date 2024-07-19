package com.atguigu.gmall.feign.cart;

import com.atguigu.gmall.cart.entity.CartItem;
import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.fallback.CartFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "service-cart", fallback = CartFeignClientFallback.class)
public interface CartFeignClient {

    @GetMapping(value = "/api/inner/cart/addCart/{skuId}/{skuNum}")
    public Result<AddCartSuccessVo> addCart(@PathVariable(value = "skuId") Long skuId ,
                                            @PathVariable(value = "skuNum")Integer skuNum) ;

    @GetMapping(value = "/api/inner/cart/cart/deleteChecked")
    public Result deleteCheckedCart() ;

    @GetMapping(value = "/api/inner/cart/getCheckedCartItem")
    public Result<List<CartItem>> getCheckedCartItem() ;

}
