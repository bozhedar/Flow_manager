package org.flow_manager.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinIOConfig {
    @Value("${minio.buckets.pdf-bucket}")
    private String PDF_BUCKET_NAME;
    @Value("${minio.buckets.non-pdf-bucket}")
    private String NON_PDF_BUCKET_NAME;
    @Value("${minio.user}")
    private String username;
    @Value("${minio.password}")
    private String password;
    @Value("${minio.port}")
    private int port;

    @Bean
    public MinioClient minioClient() throws Exception {
        return MinioClient.builder()
                .credentials(username, password)
                .endpoint("http://localhost:" + port)
                .build();
    }

}

