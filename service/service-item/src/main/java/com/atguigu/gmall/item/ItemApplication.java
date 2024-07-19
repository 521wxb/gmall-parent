package com.atguigu.gmall.item;

import com.atguigu.gmall.cache.anno.EnableGmallCacheAspect;
import com.atguigu.gmall.common.annotation.EnableThreadpool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 商品的详情服务
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign.product",
        "com.atguigu.gmall.feign.search"
})
@EnableGmallCacheAspect
@EnableThreadpool
public class ItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemApplication.class , args) ;
    }

}
