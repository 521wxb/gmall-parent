package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.BaseTrademark;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【base_trademark(品牌表)】的数据库操作Service
* @createDate 2023-02-07 11:49:36
*/
public interface BaseTrademarkService extends IService<BaseTrademark> {

    /**
     * 分页查询
     * @param pageNo
     * @param size
     * @return
     */
    public abstract Page findTrademarkPage(Integer pageNo, Integer size);

    /**
     * 根据品牌的id进行删除
     * @param trademarkId
     */
    public abstract void deleteById(Long trademarkId);
}
