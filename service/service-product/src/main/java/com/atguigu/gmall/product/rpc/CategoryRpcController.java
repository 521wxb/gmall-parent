package com.atguigu.gmall.product.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.biz.CategoryBizService;
import com.atguigu.gmall.web.vo.CategoryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/inner/product")   // 所有的内部业务接口的请求路径的规则：/api/inner/模块名称
public class CategoryRpcController {

    @Autowired
    private CategoryBizService categoryBizService ;

    @GetMapping(value = "/category/trees")
    public Result<List<CategoryVo>> findAllCategory() {
        log.info("CategoryRpcController....findAllCategory....方法执行了");
//        try {
//            Thread.sleep(2000);         // 模拟超时
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        List<CategoryVo> categoryVoList = categoryBizService.findAllCategory();
        return Result.build(categoryVoList , ResultCodeEnum.SUCCESS) ;
    }

}

