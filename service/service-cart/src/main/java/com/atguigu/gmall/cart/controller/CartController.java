package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.biz.CartBizService;
import com.atguigu.gmall.cart.entity.CartItem;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/cart")
public class CartController {

    @Autowired
    private CartBizService cartBizService ;

    @GetMapping(value = "/cartList")
    public Result<List<CartItem>> cartList() {
        List<CartItem> cartItemList = cartBizService.findCartList() ;
        return Result.build(cartItemList , ResultCodeEnum.SUCCESS) ;
    }

    @PostMapping(value = "/addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable(value = "skuId") Long skuId , @PathVariable(value = "skuNum") Integer skuNum) {
        cartBizService.addToCart(skuId , skuNum) ;
        return Result.ok() ;
    }

    @GetMapping(value = "/checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable(value = "skuId") Long skuId , @PathVariable(value = "isChecked") Integer isChecked) {
        cartBizService.checkCart(skuId , isChecked) ;
        return Result.ok() ;
    }

    @DeleteMapping(value = "/deleteCart/{skuId}")
    public Result deleteCart(@PathVariable(value = "skuId") Long skuId) {
        cartBizService.deleteCart(skuId) ;
        return Result.ok() ;
    }

}
