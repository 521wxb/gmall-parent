package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.dto.SkuInfoDTO;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2023-02-07 11:49:36
*/
public interface SkuInfoService extends IService<SkuInfo> {

    /**
     * SkuInfo的分页查询
     * @param pageNo
     * @param pageSize
     * @return
     */
    public abstract Page findByPage(Integer pageNo, Integer pageSize);

    /**
     * 保存skuInfo的数据
     * @param skuInfoDTO
     */
    public abstract void saveSkuInfo(SkuInfoDTO skuInfoDTO);

    /**
     * 上架
     * @param skuId
     */
    public abstract void onSale(Long skuId);

    /**
     * 下架
     * @param skuId
     */
    public abstract void cancelSale(Long skuId);

}
