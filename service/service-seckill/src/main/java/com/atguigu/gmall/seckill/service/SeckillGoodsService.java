package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.seckill.entity.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
* @author Administrator
* @description 针对表【seckill_goods】的数据库操作Service
* @createDate 2023-03-04 09:06:15
*/
public interface SeckillGoodsService extends IService<SeckillGoods> {

    /**
     * 查询指定日期的参与秒杀商品的数据
     * @param date
     * @return
     */
    public abstract List<SeckillGoods> selectAllToDaySeckillGoods(Date date);

    /**
     * 向Redis中存储指定日期的秒杀商品数据
     * @param date
     */
    public abstract void saveSeckillGoodsToRedis(Date date , List<SeckillGoods> seckillGoodsList);

    /**
     * 存储数据到本地缓存
     * @param seckillGoodsList
     */
    public abstract void saveSeckillGoodsToLocalCache(List<SeckillGoods> seckillGoodsList);

    /**
     * 生成秒杀码
     * @param skuId
     * @return
     */
    public abstract String genSeckillCode(Long skuId);

    /**
     * 秒杀排队
     * @param skuId
     * @param skuIdStr
     */
    public abstract void queue(Long skuId, String skuIdStr);

    /**
     * 检查订单的状态
     * @param skuId
     * @return
     */
    public abstract ResultCodeEnum checkOrderStatus(Long skuId);
}
