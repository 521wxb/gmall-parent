package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.BaseCategory2;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/product")
@Api(value = "二级分类的类")
public class BaseCategory2Controller {

    @Autowired
    private BaseCategory2Service baseCategory2Service ;

    @ApiOperation(value = "根据一级分类的id查询所有的二级分类")
    @GetMapping(value = "/getCategory2/{category1Id}")
    public Result<List<BaseCategory2>> findByCategory1Id(@ApiParam(value = "一级分类的id") @PathVariable(value = "category1Id") Long c1Id) {
        List<BaseCategory2> baseCategory2List = baseCategory2Service.findByCategory1Id(c1Id) ;
        return Result.ok(baseCategory2List) ;
    }


}
