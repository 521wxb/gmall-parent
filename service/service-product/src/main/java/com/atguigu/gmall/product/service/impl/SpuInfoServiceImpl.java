package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.dto.SpuInfoDTO;
import com.atguigu.gmall.product.entity.*;
import com.atguigu.gmall.product.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【spu_info(商品表)】的数据库操作Service实现
* @createDate 2023-02-07 11:49:36
*/
@Slf4j
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo> implements SpuInfoService{

//    @Autowired
//    private SpuInfoMapper spuInfoMapper ;

    @Autowired
    private SpuImageService spuImageService ;

    @Autowired
    private SpuSaleAttrService spuSaleAttrService ;

    @Autowired
    private BaseSaleAttrService baseSaleAttrService ;

    @Autowired
    private SpuSaleAttrValueService spuSaleAttrValueService ;

    @Override
    public Page findByPage(Integer pageNo, Integer pageSize, Long category3Id) {

        // 调用IService接口中的page方法
        Page page = new Page(pageNo , pageSize) ;
        LambdaQueryWrapper<SpuInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        lambdaQueryWrapper.eq(SpuInfo::getCategory3Id , category3Id) ;
        page(page , lambdaQueryWrapper) ;

        // 直接返回page对象
        return page;

    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuInfoDTO spuInfoDTO) {
        log.info("保存spuInfo数据接口方法执行了...........");

        // 保存SpuInfo数据
        SpuInfo spuInfo = new SpuInfo() ;
//        spuInfo.setSpuName(spuInfoDTO.getSpuName());
//        spuInfo.setDescription(spuInfoDTO.getDescription());
//        spuInfo.setCategory3Id(spuInfoDTO.getCategory3Id());
//        spuInfo.setTmId(spuInfoDTO.getTmId());
        // 进行属性执行的copy，属性名称相同的并且属性的类型相同的属性值
        BeanUtils.copyProperties(spuInfoDTO , spuInfo);
        save(spuInfo) ;

        // 获取spuInfo的id
        Long spuInfoId = spuInfo.getId();

        // 保存图片数据
        List<SpuImage> spuImageList = spuInfoDTO.getSpuImageList();

        /**
         * map方法的作用：是对流中的数据类型进行转换的
         */
        spuImageList = spuImageList.stream().map(spuImage -> {
            spuImage.setSpuId(spuInfoId);
            return spuImage ;
        }).collect(Collectors.toList()) ;
        spuImageService.saveBatch(spuImageList) ;

        // 保存销售属性
        List<SpuSaleAttr> spuSaleAttrList = spuInfoDTO.getSpuSaleAttrList();
        spuSaleAttrList = spuSaleAttrList.stream().map(spuSaleAttr -> {
            spuSaleAttr.setSpuId(spuInfoId);
            return spuSaleAttr ;
        }).collect(Collectors.toList()) ;
        spuSaleAttrService.saveBatch(spuSaleAttrList) ;

        // 保存销售属性值
        List<SpuSaleAttr> dtoSpuSaleAttrList = spuInfoDTO.getSpuSaleAttrList();
        dtoSpuSaleAttrList.stream().forEach(spuSaleAttr -> {

            // 获取每一个销售属性的值的集合
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            spuSaleAttrValueList = spuSaleAttrValueList.stream().map(spuSaleAttrValue -> {

                spuSaleAttrValue.setSpuId(spuInfoId);

                // 获取销售属性的id
                Long baseSaleAttrId = spuSaleAttrValue.getBaseSaleAttrId();
                BaseSaleAttr baseSaleAttr = baseSaleAttrService.getById(baseSaleAttrId);
                spuSaleAttrValue.setSaleAttrName(baseSaleAttr.getName());

                return spuSaleAttrValue ;

            }).collect(Collectors.toList()) ;
            spuSaleAttrValueService.saveBatch(spuSaleAttrValueList) ;

        });

    }

}




