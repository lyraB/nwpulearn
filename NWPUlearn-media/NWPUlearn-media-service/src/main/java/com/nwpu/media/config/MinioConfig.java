package com.nwpu.media.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yfh
 * @version 1.0
 * @description Minio配置
 * @date 2023/2/22
 */
@Configuration
public class MinioConfig {
    @Value("${minio.endpoint}") // 读取配置文件中的：对象存储服务的URL
    private String endpoint;
    @Value("${minio.accessKey}")// 用户ID
    private String accessKey;
    @Value("${minio.secretKey}")// 密码
    private String secretKey;

    @Bean
    public MinioClient minioClient() {

        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();
        return minioClient;
    }

}
