package com.atguigu.gmall.search.biz;

import com.atguigu.gmall.search.dto.SearchParamDTO;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.vo.SearchResponseVo;

public interface GoodsBizService {

    /**
     * 保存数据到ES索引库中
     * @param goods
     */
    public abstract void saveGoods(Goods goods);

    /**
     * 根据skuId删除数据
     * @param skuId
     */
    public abstract void deleteGoodsById(Long skuId);

    /**
     * 实现高级搜索
     * @param searchParamDTO
     * @return
     */
    public abstract SearchResponseVo search(SearchParamDTO searchParamDTO);

    /**
     * 更新ES索引库中的商品的热度分
     * @param skuId
     * @param hotScore
     */
    public abstract void updateHotScore(Long skuId, Long hotScore);

}
