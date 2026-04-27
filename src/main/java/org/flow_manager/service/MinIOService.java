package org.flow_manager.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public InputStream readFromBucket(String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(sourceBucket)
                        .object(objectName)
                        .build()
        );
    }

    public void writeToBucket(String objectName, byte[] data) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(targetBucket)
                        .object(objectName)
                        .stream(new ByteArrayInputStream(data), (long) data.length, (long) -1)
                        .contentType("application/pdf")
                        .build()
        );
    }
}
