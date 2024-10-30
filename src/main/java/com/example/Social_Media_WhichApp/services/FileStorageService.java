package com.example.Social_Media_WhichApp.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


// file này hiện tại chưa cần thiết
@Service
public class FileStorageService {

   public  String unitSubString;

    @Value("${upload.dir}")
    private String uploadDir;


    public String save_File(MultipartFile file) throws IOException {
       unitSubString = UUID.randomUUID().toString() + file.getOriginalFilename();
        if(file.isEmpty()){
            throw new IOException("File ís Emty");
        }

        Path path = Paths.get(uploadDir + unitSubString);
        Files.copy(file.getInputStream(), path);
        return file.getOriginalFilename();
    }

    public String provider_RandomString(){
        return unitSubString;
    }
}
