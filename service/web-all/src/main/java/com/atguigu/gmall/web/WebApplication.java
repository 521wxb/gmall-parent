package com.atguigu.gmall.web;

import com.atguigu.gmall.common.annotation.EnableFeignClientInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * exclude表示排除某一个类，由于web-all不需要访问数据库，因此不需要数据源的自动化配置
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign.item",
        "com.atguigu.gmall.feign.product",
        "com.atguigu.gmall.feign.search",
        "com.atguigu.gmall.feign.cart",
        "com.atguigu.gmall.feign.order",
        "com.atguigu.gmall.feign.seckill"
})
@EnableFeignClientInterceptor
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class , args) ;
    }

}
