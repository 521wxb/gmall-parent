package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.CategoryFeignClient;
import com.atguigu.gmall.web.vo.CategoryVo;
import feign.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Autowired
    private CategoryFeignClient categoryFeignClient ;

    /**
     * 方法的返回值表示的是页面路径
     */
    @GetMapping(value = {"/" , "/index.html"})
    public String index(Model model) {

        // 调用其他微服务的接口获取数据，然后把数据存储到Model数据模型中
        // TODO 远程调用service-product接口查询所有分类数据
        // Request.Options options = new Request.Options(3 , TimeUnit.SECONDS , 3 , TimeUnit.SECONDS , false);
        Result<List<CategoryVo>> result = categoryFeignClient.findAllCategory() ;
        model.addAttribute("list" , result.getData()) ;

        return "index/index" ;   // classpath:/templates/index/index.html
    }

}
