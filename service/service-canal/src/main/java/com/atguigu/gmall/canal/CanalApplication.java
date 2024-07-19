package com.atguigu.gmall.canal;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableCanalClient  // 声明当前这个服务是canal的客户端
public class CanalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class , args) ;
    }

}
