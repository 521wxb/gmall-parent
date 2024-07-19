package com.atguigu.gmall.product;

import com.atguigu.gmall.cache.anno.EnableGmallCacheAspect;
import com.atguigu.gmall.common.annotation.EnableMinio;
import com.atguigu.gmall.common.annotation.EnableSwagger2Configuration;
import com.atguigu.gmall.common.annotation.EnableThreadpool;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = "com.atguigu.gmall.product.mapper")
// @ComponentScan(basePackages = "com.atguigu.gmall")   // 覆盖了默认的包扫描路径
// @Import(value = GlobalExceptionHandler.class)
// @EnableGlobalExceptionHandler
//@Import(value = MinioConfiguration.class)
@EnableMinio
@EnableSwagger2Configuration
@EnableTransactionManagement
@EnableScheduling  // 开启定时任务的功能
@EnableGmallCacheAspect
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign.search"
})
@EnableThreadpool
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class , args) ;
    }

}
