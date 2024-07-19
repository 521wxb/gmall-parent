package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuInfo;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_trademark(品牌表)】的数据库操作Service实现
* @createDate 2023-02-07 11:49:36
*/
@Service
public class BaseTrademarkServiceImpl extends ServiceImpl<BaseTrademarkMapper, BaseTrademark> implements BaseTrademarkService{

    @Autowired
    private SpuInfoMapper spuInfoMapper ;

    @Autowired
    private SkuInfoMapper skuInfoMapper ;

    @Override
    public Page findTrademarkPage(Integer pageNo, Integer size) {
        Page page = new Page(pageNo , size) ;

        /**
         * 调用是IService接口中的分页查询方法，该方法执行完毕以后会将分页的结果数据存储到page对象
         */
        page(page);
        return page;
    }

    @Override
    public void deleteById(Long trademarkId) {

        // 判断当前要删除的品牌是否被spu进行引用
        LambdaQueryWrapper<SpuInfo> spuInfolambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        spuInfolambdaQueryWrapper.eq(SpuInfo::getTmId , trademarkId) ;
        List<SpuInfo> spuInfoList = spuInfoMapper.selectList(spuInfolambdaQueryWrapper);
        if(spuInfoList != null && spuInfoList.size() > 0) {
            throw new GmallException(ResultCodeEnum.REF_SPU_ERROR) ;
        }

        // 判断当前要删除的品牌是否被sku进行引用
        LambdaQueryWrapper<SkuInfo> skuInfoLambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        skuInfoLambdaQueryWrapper.eq(SkuInfo::getTmId , trademarkId) ;
        List<SkuInfo> skuInfoList = skuInfoMapper.selectList(skuInfoLambdaQueryWrapper);
        if(skuInfoList != null && skuInfoList.size() > 0) {
            throw new GmallException(ResultCodeEnum.REF_SKU_ERROR) ;
        }

        // 直接进行删除
        removeById(trademarkId);

    }

}




