package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.entity.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
* @createDate 2023-02-07 11:49:36
*/
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo> implements BaseAttrInfoService{

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper ;

    @Autowired
    private BaseAttrValueService baseAttrValueService ;

    @Override
    public List<BaseAttrInfo> findByCategoryId(Long c1Id, Long c2Id, Long c3Id) {
        return baseAttrInfoMapper.findByCategoryId(c1Id , c2Id , c3Id);
    }

    @Transactional
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        // 判断baseAttrInfo对象是否有id
        if(baseAttrInfo.getId() == null) {

            /**
             * 保存平台属性的基本信息
             */
            baseAttrInfoMapper.insert(baseAttrInfo) ;

            // 保存平台属性的值
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

            /**
             * attrValueList.stream()把集合转换成流
             * attrValueList.stream().map() ----> 对流中的元素类型进行变能
             */
            List<BaseAttrValue> baseAttrInfoList = attrValueList.stream().map(baseAttrValue -> {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                return baseAttrValue;
            }).collect(Collectors.toList());

            // 批量保存
            baseAttrValueService.saveBatch(baseAttrInfoList) ;


        }else  {

            // 修改平台属性
            updateById(baseAttrInfo) ;

            // 删除原有的平台属性的值
            LambdaQueryWrapper<BaseAttrValue> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
            lambdaQueryWrapper.eq(BaseAttrValue::getAttrId , baseAttrInfo.getId()) ;
            baseAttrValueService.remove(lambdaQueryWrapper) ;

            // 新增平台属性值
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            List<BaseAttrValue> baseAttrValueList = attrValueList.stream().map(baseAttrValue -> {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                return baseAttrValue;
            }).collect(Collectors.toList());

            // 批量保存
            baseAttrValueService.saveBatch(baseAttrValueList) ;

        }


    }

}




