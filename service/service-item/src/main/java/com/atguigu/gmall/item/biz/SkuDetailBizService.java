package com.atguigu.gmall.item.biz;

import com.atguigu.gmall.web.vo.SkuDetailVo;

public interface SkuDetailBizService {

    public abstract SkuDetailVo skuDetailBySkuId(Long skuId);

    public abstract void updateHotScore(Long skuId);

}
