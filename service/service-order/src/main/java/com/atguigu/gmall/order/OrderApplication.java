package com.atguigu.gmall.order;

import com.atguigu.gmall.common.annotation.EnableFeignClientInterceptor;
import com.atguigu.gmall.rabbit.anno.EnableRabbit;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan(basePackages = "com.atguigu.gmall.order.mapper")
@EnableFeignClientInterceptor
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign.cart",
        "com.atguigu.gmall.feign.user",
        "com.atguigu.gmall.feign.product",
        "com.atguigu.gmall.feign.ware"
})
@EnableRabbit
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class , args) ;
    }

}
