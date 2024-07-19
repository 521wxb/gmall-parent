package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.BaseAttrInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_attr_info(属性表)】的数据库操作Service
* @createDate 2023-02-07 11:49:36
*/
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {

    public abstract List<BaseAttrInfo> findByCategoryId(Long c1Id, Long c2Id, Long c3Id);

    public abstract void saveAttrInfo(BaseAttrInfo baseAttrInfo);
}
