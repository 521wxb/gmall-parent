package com.atguigu.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.SpuImage;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.mapper.SpuImageMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【spu_image(商品图片表)】的数据库操作Service实现
* @createDate 2023-02-07 11:49:36
*/
@Service
public class SpuImageServiceImpl extends ServiceImpl<SpuImageMapper, SpuImage> implements SpuImageService{

    @Override
    public List<SpuImage> findBySpuId(Long spuId) {
        LambdaQueryWrapper<SpuImage> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        lambdaQueryWrapper.eq(SpuImage::getSpuId , spuId) ;
        List<SpuImage> spuImageList = list(lambdaQueryWrapper);
        return spuImageList;
    }

}




