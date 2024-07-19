package com.atguigu.gmall.seckill.biz;

import com.atguigu.gmall.seckill.entity.SeckillGoods;

import java.util.List;

public interface SeckillBizService {

    /**
     * 查询当前参与秒杀的所有的商品数据
     * @return
     */
    public abstract List<SeckillGoods> findAllToDaySeckillGoods();

    /**
     * 根据skuId的查询秒杀商品数据
     * @param skuId
     * @return
     */
    public abstract SeckillGoods findSeckillGoodsBySkuId(Long skuId);
}
