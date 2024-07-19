package com.atguigu.gmall.common.config;

import com.atguigu.gmall.common.properties.MinioProperties;
import com.atguigu.gmall.common.service.FileUploadService;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = MinioProperties.class)   // 开启通过实体类型封装配置文件中内容的功能
public class MinioConfiguration {

    @Autowired
    private MinioProperties minioProperties ;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build() ;
        return minioClient ;
    }

    /**
     * 方法上所定义的参数，spring在调用该方法的时候会从spring容器中获取指定的对象，然后作为方法参数传递过来
     * @param minioProperties
     * @param minioClient
     * @return
     */
    @Bean
    public FileUploadService fileUploadService(MinioProperties minioProperties , MinioClient minioClient) {
        return new FileUploadService(minioProperties , minioClient) ;
    }

}
