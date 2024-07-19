package com.atguigu.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.BaseCategory2;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category2(二级分类表)】的数据库操作Service实现
* @createDate 2023-02-07 11:49:36
*/
@Service
public class BaseCategory2ServiceImpl extends ServiceImpl<BaseCategory2Mapper, BaseCategory2> implements BaseCategory2Service{

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper ;

    @Override
    public List<BaseCategory2> findByCategory1Id(Long c1Id) {

        LambdaQueryWrapper<BaseCategory2> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;

        /**
         * BaseCategory2::getCategory1Id表示的意思就是使用category1Id字段进行条件查询
         */
        lambdaQueryWrapper.eq(BaseCategory2::getCategory1Id , c1Id) ;
        List<BaseCategory2> baseCategory2List = baseCategory2Mapper.selectList(lambdaQueryWrapper);

        return baseCategory2List;
    }

}




