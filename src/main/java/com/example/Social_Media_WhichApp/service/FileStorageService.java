package com.example.Social_Media_WhichApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class FileStorageService {

//    private final String upload_dir = "src/main/resources/static/uploads/";
//
//    public String save_File(MultipartFile file) throws IOException {
//
//        // Lấy tên file gốc
//        String fileName = file.getOriginalFilename();
//        String filePath = upload_dir + fileName;
//        Path path = Paths.get(filePath);
//
//        // lưu file vào thư mục đã chỉ định
//        Files.copy(file.getInputStream(), path);
//
//        // trả về tên file
//        return fileName;
//
//    }

    @Value("${upload.dir}")
    private String uploadDir;

    public String save_File(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        // Lưu file vào thư mục đã chỉ định
        Path path = Paths.get(uploadDir + file.getOriginalFilename());
        Files.copy(file.getInputStream(), path);

        // Trả về URL công khai để truy cập ảnh
        return file.getOriginalFilename();
    }

}
