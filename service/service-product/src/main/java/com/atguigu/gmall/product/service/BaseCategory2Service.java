package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.BaseCategory2;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category2(二级分类表)】的数据库操作Service
* @createDate 2023-02-07 11:49:36
*/
public interface BaseCategory2Service extends IService<BaseCategory2> {

    /**
     * 根据一级分类的id查询所有的二级分类
     * @param c1Id
     * @return
     */
    public abstract List<BaseCategory2> findByCategory1Id(Long c1Id);

}
