package com.cornidezstudios.media.controllers;

import com.cornidezstudios.media.services.FileStorageService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileStorageController {

    private final FileStorageService fileStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadFile(@RequestParam("file") MultipartFile[] files) throws IOException {
        for (MultipartFile f : files) {
            fileStorageService.saveFile(f);
        }
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws IOException {
        Resource file = fileStorageService.loadFile(filename);
        return ResponseEntity.ok(file);
    }

    @PutMapping("/{filename}")
    public void replaceFile(MultipartFile file) throws IOException {
        fileStorageService.saveFile(file);
    }

    @DeleteMapping("/{filename}")
    public void deleteFile(@PathVariable String filename) throws IOException {
        fileStorageService.deleteFile(filename);
    }
}
