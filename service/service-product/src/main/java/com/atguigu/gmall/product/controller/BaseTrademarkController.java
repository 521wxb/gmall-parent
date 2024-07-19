package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.entity.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/product")
public class BaseTrademarkController {

    @Autowired
    private BaseTrademarkService baseTrademarkService ;

    @GetMapping(value = "/baseTrademark/{page}/{limit}")
    public Result<Page> findTrademarkPage(@PathVariable(value = "page") Integer pageNo , @PathVariable(value = "limit") Integer size) {
        Page page = baseTrademarkService.findTrademarkPage(pageNo , size) ;
        return Result.ok(page) ;
    }

    @PostMapping(value = "/baseTrademark/save")
    public Result saveTrademark(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.save(baseTrademark) ;
        return Result.build(null , ResultCodeEnum.SUCCESS) ;
    }

    @GetMapping(value = "/baseTrademark/get/{trademarkId}")
    public Result<BaseTrademark> findById(@PathVariable(value = "trademarkId") Long trademarkId) {
        BaseTrademark baseTrademark = baseTrademarkService.getById(trademarkId);
        return Result.build(baseTrademark , ResultCodeEnum.SUCCESS) ;
    }

    @PutMapping(value = "/baseTrademark/update")
    public Result updateById(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.updateById(baseTrademark) ;
        return Result.build(null , ResultCodeEnum.SUCCESS) ;
    }

    @DeleteMapping(value = "/baseTrademark/remove/{trademarkId}")
    public Result deleteById(@PathVariable(value = "trademarkId") Long trademarkId ) {
        baseTrademarkService.deleteById(trademarkId) ;
        return Result.ok() ;
    }

    @GetMapping(value = "/baseTrademark/getTrademarkList")
    public Result<List<BaseTrademark>> findAll() {
        List<BaseTrademark> baseTrademarkList = baseTrademarkService.list();
        return Result.build(baseTrademarkList , ResultCodeEnum.SUCCESS) ;
    }

}
