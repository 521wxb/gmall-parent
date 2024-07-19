package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Mapper
* @createDate 2023-02-07 11:49:36
* @Entity com.atguigu.gmall.product.entity.SpuSaleAttr
*/
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    /**
     * 根据spuId查询所有的销售属性以及销售属性值
     * @param spuId
     * @return
     */
    public abstract List<SpuSaleAttr> findBySpuId(Long spuId);

    /**
     * 根据spuId查询所有的销售属性以及销售属性值
     * @param skuId
     * @return
     */
    public abstract List<SpuSaleAttr> findSpuSalAttrBySkuId(Long skuId);

}




