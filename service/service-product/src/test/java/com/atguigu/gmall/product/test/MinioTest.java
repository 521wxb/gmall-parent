package com.atguigu.gmall.product.test;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;

import java.io.FileInputStream;

public class MinioTest {

    public static void main(String[] args) {

        // 创建MinioClient对象
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://192.168.200.130:9000")
                .credentials("admin", "admin1234")
                .build() ;

        try {

            // 判断某一个桶是否存在
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket("gmall").build());

            // 判断桶是否存在
            if(!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("gmall").build());
            }

            // 调用上传文件的方法上传文件
            FileInputStream fileInputStream = new FileInputStream("D:\\images\\1.png") ;
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket("gmall")   // 指定桶的名称
                    .object("1.png")   // 指定上传以后的文件在minio中的名称
                    .stream(fileInputStream , fileInputStream.available() , -1)
                    .build() ;
            minioClient.putObject(putObjectArgs) ;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
