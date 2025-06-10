package com.cornidezstudios.media.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileStorageService {
    private final Path root = Path.of("storage","media");

    public FileStorageService() throws IOException {
        Files.createDirectories(root);
    }

    public void saveFile(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            String filename = Objects.requireNonNull(file.getOriginalFilename());
            Path destinationFile = root.resolve(filename).normalize().toAbsolutePath();
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } else {
            throw new IOException("Could not save File, it is empty");
        }
    }

    public Resource loadFile(String name) throws IOException {
        Path filePath = root.resolve(name);

        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + name);
        }

        return new UrlResource(filePath.toUri());
    }

    public void deleteFile(String name) throws IOException {
        // check if the file exists before attempting to delete it
        Path filePath = root.resolve(name);

        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + name);
        }

        // delete the existing file
        Files.deleteIfExists(filePath);
    }

}
