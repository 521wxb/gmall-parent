package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.entity.BaseCategory1;
import com.atguigu.gmall.web.vo.CategoryView;
import com.atguigu.gmall.web.vo.CategoryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category1(一级分类表)】的数据库操作Mapper
* @createDate 2023-02-07 11:49:36
* @Entity com.atguigu.gmall.product.entity.BaseCategory1
*/
public interface BaseCategory1Mapper extends BaseMapper<BaseCategory1> {

    /**
     * 查询所有的一级分类，并且会将一级分所对应的子分类查询出来，以及子分类的子分类
     * @return
     */
    public abstract List<CategoryVo> findAllCategory();

    /**
     * 根据skuId查询分类数据
     * @param skuId
     * @return
     */
    public abstract CategoryView findByCategoryBySkuId(Long skuId);

}




