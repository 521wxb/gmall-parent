package com.atguigu.gmall.seckill.mapper;

import com.atguigu.gmall.seckill.entity.SeckillGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【seckill_goods】的数据库操作Mapper
* @createDate 2023-03-04 09:06:15
* @Entity com.atguigu.gmall.seckill.entity.SeckillGoods
*/
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    public abstract List<SeckillGoods> selectAllToDaySeckillGoods(String dateStr);

}




