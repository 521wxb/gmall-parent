package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.AopService;
import com.atguigu.gmall.product.entity.SkuInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AopServiceImpl implements AopService {

    // @GmallCache
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        // int a = 1 / 0 ;
        SkuInfo skuInfo = new SkuInfo() ;
        skuInfo.setId(skuId);
        skuInfo.setSkuName("小米13 徕卡光学镜头 第二代骁龙8处理器 超窄边屏幕 120Hz高刷 67W快充 12+256GB 黑色 5G手机");
        log.info("AopServiceImpl...getSkuInfo方法执行了.........");
        return skuInfo;
    }

}
