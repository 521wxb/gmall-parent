package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.BaseCategory1;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController  // 声明当前这个类是一个controller、并且会将这个类中的所有的方法的返回值转换成json数据进行返回
@RequestMapping(value = "/admin/product")
//@CrossOrigin  // 表示运行所有的域的请求
@Api(value = "一级分类的类")
public class BaseCategory1Controller {

    @Autowired
    private BaseCategory1Service baseCategory1Service ;

    @GetMapping(value = "/getCategory1")
    @ApiOperation(value = "查询所有的一级分类")
    public Result<List<BaseCategory1>> findAllBaseCategory1() {
        List<BaseCategory1> baseCategory1List = baseCategory1Service.list();
        return Result.ok(baseCategory1List) ;
    }

}
