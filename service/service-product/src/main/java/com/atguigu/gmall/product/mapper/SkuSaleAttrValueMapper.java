package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.entity.SkuSaleAttrValue;
import com.atguigu.gmall.web.vo.AttrValueConcatVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【sku_sale_attr_value(sku销售属性值)】的数据库操作Mapper
* @createDate 2023-02-07 11:49:36
* @Entity com.atguigu.gmall.product.entity.SkuSaleAttrValue
*/
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    /**
     * 根据skuId获取它所对应的兄弟sku的销售属性值的组合
     * @param skuId
     * @return
     */
    public abstract List<AttrValueConcatVo> findBrotherSkuSaleAttrValueConcatBySkuId(Long skuId);

}




