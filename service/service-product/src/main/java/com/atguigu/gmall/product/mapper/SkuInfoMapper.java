package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.entity.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【sku_info(库存单元表)】的数据库操作Mapper
* @createDate 2023-02-07 11:49:36
* @Entity com.atguigu.gmall.product.entity.SkuInfo
*/
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    /**
     * 根据skuId查询skuInfo以及图片数据
     * @param skuId
     * @return
     */
    public abstract SkuInfo findBySkuInfoAndSkuImagesBySkuId(Long skuId);

    /**
     * 查询所有的skuId
     * @return
     */
    public abstract List<Long> findAllSkuIds();

}




