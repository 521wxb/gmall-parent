package com.atguigu.gmall.cart.rpc;

import com.atguigu.gmall.cart.biz.CartBizService;
import com.atguigu.gmall.cart.entity.CartItem;
import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/inner/cart")
public class CartRpcController {

    @Autowired
    private CartBizService cartBizService ;

    @GetMapping(value = "/addCart/{skuId}/{skuNum}")
    public Result<AddCartSuccessVo> addCart(@PathVariable(value = "skuId") Long skuId ,
                                            @PathVariable(value = "skuNum")Integer skuNum) {
        log.info("CartRpcController...addCart方法执行了,skuId: {} , skuNum: {} " , skuId , skuNum);
        AddCartSuccessVo addCartSuccessVo = cartBizService.addCart(skuId , skuNum) ;
        return Result.build(addCartSuccessVo , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/cart/deleteChecked")
    public Result deleteCheckedCart() {
        cartBizService.deleteCheckedCart() ;
        return Result.ok() ;
    }

    @GetMapping(value = "/getCheckedCartItem")
    public Result<List<CartItem>> getCheckedCartItem() {
        List<CartItem> cartItemList = cartBizService.getCheckedCartItem() ;
        return Result.build(cartItemList , ResultCodeEnum.SUCCESS) ;
    }

}
