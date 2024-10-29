package com.example.Social_Media_WhichApp.controller;
import com.example.Social_Media_WhichApp.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            String fileName = fileStorageService.save_File(file);
            response.put("message", "File uploaded successfully: " + fileName);
            response.put("fileUrl", "/uploads/" + fileName);  // Đường dẫn truy cập ảnh
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            response.put("error", "Failed to upload file: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
