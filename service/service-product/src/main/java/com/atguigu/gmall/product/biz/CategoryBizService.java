package com.atguigu.gmall.product.biz;

import com.atguigu.gmall.web.vo.CategoryVo;

import java.util.List;

public interface CategoryBizService {

    /**
     * 查询所有的分类数据包含某一个分类的子分类
     * @return
     */
    public abstract List<CategoryVo> findAllCategory() ;

}
