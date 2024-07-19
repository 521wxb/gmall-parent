package com.atguigu.gmall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service实现
* @createDate 2023-02-07 11:49:36
*/
@Service
public class SpuSaleAttrServiceImpl extends ServiceImpl<SpuSaleAttrMapper, SpuSaleAttr> implements SpuSaleAttrService{

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper ;

    @Override
    public List<SpuSaleAttr> findBySpuId(Long spuId) {
        return spuSaleAttrMapper.findBySpuId(spuId);
    }

}




