package com.cornidezstudios.media.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static java.nio.file.Files.readAllBytes;
import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    FileStorageService fileStorageService;

    @BeforeEach
    void setUp() throws IOException, NoSuchFieldException, IllegalAccessException {
        fileStorageService = new FileStorageService();

        // Override the root directory to use the temporary directory for testing
        var root = FileStorageService.class.getDeclaredField("root");
        root.setAccessible(true);
        root.set(fileStorageService, tempDir);
    }

    @Test
    void saveFile_success() throws IOException {
        // Arrange
        var mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());

        // Act
        fileStorageService.saveFile(mockFile);

        // Assert
        Path savedFilePath = tempDir.resolve("test.txt");
        assertTrue(savedFilePath.toFile().exists(), "File should be saved in the temporary directory");
        assertEquals("Hello World", new String(readAllBytes(savedFilePath)), "File content should match");
    }

    @Test
    void saveFile_failure() {
        // Arrange
        var emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            fileStorageService.saveFile(emptyFile);
        }, "Expected IOException for empty file");
    }

    @Test
    void loadFile_success() throws IOException {
        // Arrange
        var mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        fileStorageService.saveFile(mockFile);

        // Act
        var loadedFile = fileStorageService.loadFile("test.txt");

        // Assert
        assertNotNull(loadedFile, "Loaded file should not be null");
        assertTrue(loadedFile.exists(), "Loaded file should exist");
        try (InputStream fileContents = loadedFile.getInputStream()) {
            assertEquals("Hello World", new String(fileContents.readAllBytes()));
        }
    }

    @Test
    void loadFile_failure() {
        // Act & Assert
        assertThrows(IOException.class, () -> {
            fileStorageService.loadFile("nonexistent.txt");
        }, "Expected IOException for loading a non-existent file");
    }

    @Test
    void deleteFile_success() throws IOException {
        // Arrange
        var mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        fileStorageService.saveFile(mockFile);


        // Act
        fileStorageService.deleteFile("test.txt");

        // Assert
        Path deletedFilePath = tempDir.resolve("test.txt");
        assertFalse(deletedFilePath.toFile().exists(), "File should be deleted");
    }

    @Test
    void deleteFile_failure() throws IOException {
        // Act & Assert
        assertThrows(IOException.class, () -> {
            fileStorageService.deleteFile("nonexistent.txt");
        }, "Expected IOException for deleting a non-existent file");
    }
}