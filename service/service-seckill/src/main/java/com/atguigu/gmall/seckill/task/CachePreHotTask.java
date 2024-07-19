package com.atguigu.gmall.seckill.task;

import com.atguigu.gmall.seckill.entity.SeckillGoods;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
public class CachePreHotTask {

    @Autowired
    private SeckillGoodsService seckillGoodsService ;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor ;

    @Scheduled(cron = "0 0 2 * * ?")            // 凌晨两点执行一次
    public void cachePreHot() {

        // 查询数据，获取当天参与秒杀的商品数据
        List<SeckillGoods> seckillGoodsList = seckillGoodsService.selectAllToDaySeckillGoods(new Date()) ;
        log.info("从数据库查询到满足条件的秒杀商品数据.....{} " , seckillGoodsList);

        // 把查询到的数据存储到Redis中
        threadPoolExecutor.execute(() -> {
            seckillGoodsService.saveSeckillGoodsToRedis(new Date() , seckillGoodsList) ;
            log.info(" 把从数据库中查询到的数据存储到Redis中.....{} " , seckillGoodsList);
        });

        // 把查询到的数据存储到本地缓存中
        threadPoolExecutor.execute(() -> {
            seckillGoodsService.saveSeckillGoodsToLocalCache(seckillGoodsList) ;
            log.info(" 把从数据库中查询到的数据存储到本地缓存中.....{} " , seckillGoodsList);
        });

    }

}
