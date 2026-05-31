package com.sinapipro.api.shared.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@ConditionalOnProperty(name = "sinapipro.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private final Path root;

    public LocalStorageService(@Value("${sinapipro.storage.path:./uploads}") String path) {
        this.root = Path.of(path);
        try { Files.createDirectories(root); } catch (IOException e) { throw new UncheckedIOException(e); }
    }

    @Override
    public String store(String path, byte[] content, String contentType) {
        try {
            var fullPath = root.resolve(path);
            Files.createDirectories(fullPath.getParent());
            Files.write(fullPath, content);
            return path;
        } catch (IOException e) { throw new UncheckedIOException(e); }
    }

    @Override
    public InputStream retrieve(String path) {
        try { return Files.newInputStream(root.resolve(path)); }
        catch (IOException e) { throw new UncheckedIOException(e); }
    }

    @Override
    public void delete(String path) {
        try { Files.deleteIfExists(root.resolve(path)); }
        catch (IOException e) { throw new UncheckedIOException(e); }
    }

    @Override
    public String getPresignedUrl(String path, int expirationMinutes) {
        return "/api/v1/documents/download/" + path;
    }
}
