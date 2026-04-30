package org.flow_manager.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@RequiredArgsConstructor
@Getter
@Service
public class MinIOService {
    @Value("${minio.buckets.non-pdf-bucket}")
    private String sourceBucket;
    @Value("${minio.buckets.pdf-bucket}")
    private String targetBucket;
    private final MinioClient minioClient;

    public InputStream readFromPdfBucket(String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(sourceBucket)
                        .object(objectName)
                        .build()
        );
    }

    public void writeToBucket(String objectName, MultipartFile file) throws Exception {
        try (InputStream stream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(sourceBucket)
                    .object(objectName)
                    .stream(stream, file.getSize(), -1L)
                    .contentType(file.getContentType())
                    .build());
        }
    }
}
