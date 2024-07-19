package com.atguigu.gmall.product.test;

import com.atguigu.gmall.product.ProductApplication;
import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.service.SkuImageService;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest(classes = ProductApplication.class)
public class ShardingSphereTest {

    @Autowired
    private SkuImageMapper skuImageMapper ;

    @Test
    public void testShardingSphere() {

        // 向SkuImage表中插入数据
        SkuImage skuImage = new SkuImage() ;
        skuImage.setSkuId(90L);
        skuImage.setImgName("atguigu");
        skuImage.setImgUrl("atguigu");
        skuImage.setSpuImgId(11L);
        skuImage.setIsDefault("0");

        // 保存数据
        skuImageMapper.insert(skuImage) ;

        // 设置读取数据的位置为master
        HintManager.getInstance().setWriteRouteOnly();
        SkuImage select = skuImageMapper.selectById(skuImage.getId());
        System.out.println(select);

    }

    @Test
    public void testShardingSphereRead() {

        for(int x = 0 ; x < 10 ; x++) {
            // 向SkuImage表中插入数据
            SkuImage skuImage = skuImageMapper.selectById(290L);
            System.out.println(skuImage);
        }


    }

}
