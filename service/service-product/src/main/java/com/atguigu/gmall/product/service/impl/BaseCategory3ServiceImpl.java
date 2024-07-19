package com.atguigu.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category3(三级分类表)】的数据库操作Service实现
* @createDate 2023-02-07 11:49:36
*/
@Service
public class BaseCategory3ServiceImpl extends ServiceImpl<BaseCategory3Mapper, BaseCategory3> implements BaseCategory3Service{

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper ;

    @Override
    public List<BaseCategory3> findByCategory2Id(Long c2Id) {
        LambdaQueryWrapper<BaseCategory3> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        lambdaQueryWrapper.eq(BaseCategory3::getCategory2Id , c2Id) ;
        List<BaseCategory3> baseCategory3List = baseCategory3Mapper.selectList(lambdaQueryWrapper);
        return baseCategory3List;
    }

}




