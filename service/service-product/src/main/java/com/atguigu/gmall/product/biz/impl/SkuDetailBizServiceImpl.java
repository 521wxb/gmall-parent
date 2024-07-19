package com.atguigu.gmall.product.biz.impl;

import com.atguigu.gmall.product.biz.SkuDetailBizService;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.entity.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.web.vo.AttrValueConcatVo;
import com.atguigu.gmall.web.vo.CategoryView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkuDetailBizServiceImpl implements SkuDetailBizService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper ;

    @Autowired
    private SkuInfoMapper skuInfoMapper ;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper ;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper ;

    @Override
    public CategoryView findByCategoryBySkuId(Long skuId) {
        return baseCategory1Mapper.findByCategoryBySkuId(skuId);
    }

    @Override
    public SkuInfo findBySkuInfoAndSkuImagesBySkuId(Long skuId) {
        return skuInfoMapper.findBySkuInfoAndSkuImagesBySkuId(skuId) ;
    }

    @Override
    public SkuInfo findSkuInfoBySkuId(Long skuId) {
        return skuInfoMapper.selectById(skuId);
    }

    @Override
    public List<SpuSaleAttr> findSpuSalAttrBySkuId(Long skuId) {
        return spuSaleAttrMapper.findSpuSalAttrBySkuId(skuId) ;
    }

    @Override
    public List<AttrValueConcatVo> findBrotherSkuSaleAttrValueConcatBySkuId(Long skuId) {
        return skuSaleAttrValueMapper.findBrotherSkuSaleAttrValueConcatBySkuId(skuId);
    }

    @Override
    public List<Long> findAllSkuIds() {
        return skuInfoMapper.findAllSkuIds();
    }

}
