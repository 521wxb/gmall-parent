package com.atguigu.gmall.payment;

import com.atguigu.gmall.common.annotation.EnableFeignClientInterceptor;
import com.atguigu.gmall.payment.properties.AliPayProperties;
import com.atguigu.gmall.rabbit.anno.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(value = { AliPayProperties.class })
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign.order"
})
@EnableFeignClientInterceptor
@EnableRabbit
public class PaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class , args) ;
    }

}
