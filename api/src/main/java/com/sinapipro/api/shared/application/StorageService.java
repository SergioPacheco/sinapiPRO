package com.sinapipro.api.shared.application;

import java.io.InputStream;

/**
 * Storage abstraction. Implementations: LocalStorageService (dev), S3StorageService (prod).
 * Active implementation selected via spring.profiles.active or sinapipro.storage.type property.
 */
public interface StorageService {
    String store(String path, byte[] content, String contentType);
    InputStream retrieve(String path);
    void delete(String path);
    String getPresignedUrl(String path, int expirationMinutes);
}
