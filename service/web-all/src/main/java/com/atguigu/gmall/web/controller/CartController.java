package com.atguigu.gmall.web.controller;

import brave.http.HttpServerRequest;
import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 如何实现在web-all#addCart方法中和FeignClientInterceptor中共享HttpServletRequest？
 */
@Controller
public class CartController {

    @Autowired
    private CartFeignClient cartFeignClient ;

    // 定义一个Map，作用就是在CartController类和FeignClientInterceptor接口中共享HttpServletRequest
    public static final ConcurrentHashMap<Thread , HttpServletRequest>  concurrentHashMap = new ConcurrentHashMap<>() ;

    // 定义ThreadLocal对象
    public static final ThreadLocal<HttpServletRequest> threadLocal = new ThreadLocal<>() ;

    @GetMapping(value = "/addCart.html")
    public String addCart(@RequestParam(value = "skuId") Long skuId , @RequestParam(value = "skuNum") Integer skuNum , Model model) {

        System.out.println("skuId:" + skuId);
        System.out.println("skuNum:" + skuNum);

        // concurrentHashMap.put(Thread.currentThread() , request) ;
        // threadLocal.set(request);

        // 远程调用service-cart微服务中添加商品都购物车接口
        Result<AddCartSuccessVo> cartSuccessVoResult = cartFeignClient.addCart(skuId, skuNum);
        AddCartSuccessVo cartSuccessVo = cartSuccessVoResult.getData();

        if(cartSuccessVo == null) {
            model.addAttribute("msg" , cartSuccessVoResult.getMessage()) ;
            return "cart/error" ;
        }

        // 把数据存储到数据模型中
        model.addAttribute("skuInfo" , cartSuccessVo.getSkuInfo()) ;
        model.addAttribute("skuNum" , cartSuccessVo.getSkuNum()) ;

        // 返回
        return "cart/addCart" ;
    }

    @GetMapping(value = "/cart.html")
    public String cartList() {
        return "cart/index" ;
    }

    @GetMapping(value = "/cart/deleteChecked")
    public String deleteCheckedCart() {
        // 远程调用service-cart删除选中状态的购物项接口
        cartFeignClient.deleteCheckedCart() ;
        return "cart/index" ;
    }

}
