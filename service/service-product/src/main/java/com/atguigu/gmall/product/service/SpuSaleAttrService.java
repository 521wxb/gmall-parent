package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service
* @createDate 2023-02-07 11:49:36
*/
public interface SpuSaleAttrService extends IService<SpuSaleAttr> {

    /**
     * 根据spuId的查询所有的销售属性以及销售属性值
     * @param spuId
     * @return
     */
    public abstract List<SpuSaleAttr> findBySpuId(Long spuId);

}
