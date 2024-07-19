package com.atguigu.gmall.seckill;

import com.atguigu.gmall.cache.anno.EnableGmallCacheAspect;
import com.atguigu.gmall.common.annotation.EnableThreadpool;
import com.atguigu.gmall.rabbit.anno.EnableRabbit;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages = "com.atguigu.gmall.seckill.mapper")
@EnableScheduling
@EnableThreadpool
@EnableGmallCacheAspect
@EnableRabbit
public class SeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class , args) ;
    }

}
