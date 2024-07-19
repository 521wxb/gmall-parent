package com.atguigu.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_attr_value(属性值表)】的数据库操作Service实现
* @createDate 2023-02-07 11:49:36
*/
@Service
public class BaseAttrValueServiceImpl extends ServiceImpl<BaseAttrValueMapper, BaseAttrValue> implements BaseAttrValueService{

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper ;

    @Override
    public List<BaseAttrValue> findByArrId(Long attrId) {
        LambdaQueryWrapper<BaseAttrValue> baseAttrValueLambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        baseAttrValueLambdaQueryWrapper.eq(BaseAttrValue::getAttrId , attrId) ;
        List<BaseAttrValue> attrValueList = baseAttrValueMapper.selectList(baseAttrValueLambdaQueryWrapper);
        return attrValueList;
    }

}




