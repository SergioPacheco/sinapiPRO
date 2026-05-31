package com.sinapipro.api.shared.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;

@Service
@ConditionalOnProperty(name = "sinapipro.storage.type", havingValue = "s3")
public class S3StorageService implements StorageService {

    private final S3Client s3;
    private final S3Presigner presigner;
    private final String bucket;

    public S3StorageService(S3Client s3, S3Presigner presigner,
                            @Value("${sinapipro.storage.s3.bucket}") String bucket) {
        this.s3 = s3;
        this.presigner = presigner;
        this.bucket = bucket;
    }

    @Override
    public String store(String path, byte[] content, String contentType) {
        s3.putObject(PutObjectRequest.builder()
                .bucket(bucket).key(path).contentType(contentType).build(),
                RequestBody.fromBytes(content));
        return path;
    }

    @Override
    public InputStream retrieve(String path) {
        return s3.getObject(GetObjectRequest.builder().bucket(bucket).key(path).build());
    }

    @Override
    public void delete(String path) {
        s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(path).build());
    }

    @Override
    public String getPresignedUrl(String path, int expirationMinutes) {
        var request = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
                .getObjectRequest(GetObjectRequest.builder().bucket(bucket).key(path).build())
                .build();
        return presigner.presignGetObject(request).url().toString();
    }
}
