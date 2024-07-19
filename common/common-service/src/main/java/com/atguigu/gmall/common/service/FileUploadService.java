package com.atguigu.gmall.common.service;

import com.atguigu.gmall.common.properties.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

public class FileUploadService {

    // @Value("${app.minio.endpoint}")    通过Value注解可以读取配置文件
    // private String endpoint ;

    private MinioProperties minioProperties ;
    private MinioClient minioClient ;

    public FileUploadService(MinioProperties minioProperties, MinioClient minioClient) {
        this.minioClient = minioClient ;
        this.minioProperties = minioProperties ;
    }

    /**
     * 返回值表示的是上传以后的文件在minio中的访问路径
     * MultipartFile: 封装的文件数据
     */
    public String uploadFile(MultipartFile multipartFile) {

        try {

            // 判断某一个桶是否存在
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucket()).build());

            // 判断桶是否存在
            if(!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucket()).build());
            }

            // 调用上传文件的方法上传文件
            InputStream fileInputStream = multipartFile.getInputStream();

            // 生成一个uuid作为文件名称
            String fileNameExtName = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            String fileName = UUID.randomUUID().toString().replace("-" , "") + "." + fileNameExtName ;

            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(minioProperties.getBucket())   // 指定桶的名称
                    .object(fileName)   // 指定上传以后的文件在minio中的名称
                    .stream(fileInputStream , fileInputStream.available() , -1)
                    .build() ;
            minioClient.putObject(putObjectArgs) ;

            // 返回文件的访问路径
            String fileUrl = minioProperties.getEndpoint() + "/" + minioProperties.getBucket() +  "/" + fileName ;

            return fileUrl ;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null ;

    }

}
