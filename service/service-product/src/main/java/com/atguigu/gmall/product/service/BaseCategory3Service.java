package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.BaseCategory3;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category3(三级分类表)】的数据库操作Service
* @createDate 2023-02-07 11:49:36
*/
public interface BaseCategory3Service extends IService<BaseCategory3> {

    /**
     * 根据二级分类的id查询所有的三级分类
     * @param c2Id
     * @return
     */
    public abstract List<BaseCategory3> findByCategory2Id(Long c2Id);

}
