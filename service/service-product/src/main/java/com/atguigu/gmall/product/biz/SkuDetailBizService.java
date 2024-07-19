package com.atguigu.gmall.product.biz;

import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.web.vo.AttrValueConcatVo;
import com.atguigu.gmall.web.vo.CategoryView;

import java.util.List;

public interface SkuDetailBizService {

    /**
     * 根据skuId的id查询其所对应的三级分类数据
     * @param skuId
     * @return
     */
    public abstract CategoryView findByCategoryBySkuId(Long skuId);

    /**
     * 根据skuId的查询skuInfo数据以及sku的图片数据
     * @param skuId
     * @return
     */
    public abstract SkuInfo findBySkuInfoAndSkuImagesBySkuId(Long skuId);

    /**
     * 根据skuId的查询skuInfo
     * @param skuId
     * @return
     */
    public abstract SkuInfo findSkuInfoBySkuId(Long skuId);

    /**
     * 根据skuId的查询spu的所有的销售属性和销售属性的值
     * @param skuId
     * @return
     */
    public abstract List<SpuSaleAttr> findSpuSalAttrBySkuId(Long skuId);

    /**
     * 根据skuId获取它所对应的兄弟sku的销售属性值的组合
     * @param skuId
     * @return
     */
    public abstract List<AttrValueConcatVo> findBrotherSkuSaleAttrValueConcatBySkuId(Long skuId);

    /**
     * 查询所有的skuId
     * @return
     */
    public abstract List<Long> findAllSkuIds();
}
