package com.cornidezstudios.media.controllers;

import com.cornidezstudios.media.services.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileStorageController.class)
class FileStorageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    FileStorageService fileStorageService;

    MockMultipartFile mockFileOne;
    MockMultipartFile mockFileTwo;

    @BeforeEach
    void setUp() {
        mockFileOne = new MockMultipartFile("file", "a.txt", "text/plain", "A".getBytes());
        mockFileTwo = new MockMultipartFile("file", "b.txt", "text/plain", "B".getBytes());
    }

    @Test
    void uploadFiles_givenMultipleFiles_thenSaved() throws Exception {
        // Act
        mockMvc.perform(multipart("/api/files").file(mockFileOne).file(mockFileTwo))
                .andExpect(status().isOk());

        // Assert
        verify(fileStorageService).saveFile(mockFileOne);
        verify(fileStorageService).saveFile(mockFileTwo);

    }

    @Test
    void getFile_givenFilename_thenLoaded() throws Exception {
        // Act
        mockMvc.perform(get("/api/files/{filename}", "a.txt"));

        // Assert
        verify(fileStorageService).loadFile("a.txt");
    }

    @Test
    void replaceFile_givenFile_thenSaved() throws Exception {
        // Act
        mockMvc.perform(multipart("/api/files/{filename}", "a.txt")
                        .file(mockFileOne)
                        .with(r -> { r.setMethod("PUT"); return r; }))
                .andExpect(status().isOk());

        // Assert
        verify(fileStorageService).saveFile(mockFileOne);
    }

    @Test
    void deleteFile_givenFilename_thenDeleted() throws Exception {
        // Act
        mockMvc.perform(delete("/api/files/{filename}", "a.txt"))
                .andExpect(status().isOk());
        // Assert
        verify(fileStorageService).deleteFile("a.txt");
    }
}