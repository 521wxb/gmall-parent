package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.dto.SpuInfoDTO;
import com.atguigu.gmall.product.entity.SpuInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【spu_info(商品表)】的数据库操作Service
* @createDate 2023-02-07 11:49:36
*/
public interface SpuInfoService extends IService<SpuInfo> {

    /**
     * SPU分页查询
     * @param pageNo
     * @param pageSize
     * @param category3Id
     * @return
     */
    public abstract Page findByPage(Integer pageNo, Integer pageSize, Long category3Id);

    /**
     * 保存spuInfo的属性
     * @param spuInfoDTO
     */
    public abstract void saveSpuInfo(SpuInfoDTO spuInfoDTO);

}
