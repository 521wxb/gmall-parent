package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.entity.SkuAttrValue;
import com.atguigu.gmall.search.entity.SearchAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Mapper
* @createDate 2023-02-07 11:49:36
* @Entity com.atguigu.gmall.product.entity.SkuAttrValue
*/
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValue> {

    /**
     * 根据skuId的查询平台属性和平台属性的值
     * @param skuId
     * @return
     */
    public abstract List<SearchAttr> findSkuAttrAndValueBySkuId(Long skuId) ;

}




