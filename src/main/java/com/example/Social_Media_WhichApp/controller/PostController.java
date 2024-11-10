package com.example.Social_Media_WhichApp.controller;


import com.example.Social_Media_WhichApp.entity.Media;
import com.example.Social_Media_WhichApp.entity.Post;
import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.repository.PostRepository;
import com.example.Social_Media_WhichApp.repository.UserRepository;
import com.example.Social_Media_WhichApp.services.FileStorageService;
import com.example.Social_Media_WhichApp.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.UUID;

@RestController
@RequestMapping("api/posts")
public class PostController {


//    public  String unitSubString;
//
//    @Value("${upload.dir}")
//    private String uploadDir;
//
//
//    public String save_File(MultipartFile file) throws IOException {
//        if(file.isEmpty()){
//            throw new IOException("File ís Emty");
//        }
//        unitSubString = UUID.randomUUID().toString();
//        Path path = Paths.get(uploadDir + unitSubString + "_"+file.getOriginalFilename());
//        Files.copy(file.getInputStream(), path);
//
//        return file.getOriginalFilename();
//    }

    @Autowired
    private PostService postService;
    @Autowired
    private FileStorageService fileStorageService;

    public PostController() throws UnknownHostException {
    }

    private String getSubRandom() {
        return fileStorageService.provider_RandomString();
    }

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public List<Post> getAllPosts(){
        return postService.getAllPosts();
    }
    InetAddress ip = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
    String ipAddress = ip.toString();

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadPost(
           @RequestParam Long user_id,
            @RequestParam List<MultipartFile> files,
            @RequestParam String content
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            Post post = new Post();
            post.setUser(new User(user_id));
            post.setContent(content);
            post.setCreated_at(new Date());

            List<Media> mediaList = new ArrayList<>();

            for (MultipartFile file : files) {
                String fileName =  fileStorageService.save_File(file);
                String fileType = fileName.endsWith(".mp4") ? "video" : "image";
//                String fileUrl = "uploads/" + unitSubString+  "_"  + fileName;

                String fileUrl = "https:/"+ ipAddress + ":8443"+"/uploads/" + getSubRandom();


                Media media = new Media(fileUrl, fileType , post);
                mediaList.add(media);
            }
            post.setMediaList(mediaList);

            // lưu post vào cơ sở dữ liệu
            Post savePost = postRepository.save(post);
            response.put("message", "Post created successfully");
            response.put("post", savePost);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            response.put("error", "Failed to upload post: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    @DeleteMapping("/{id}")
//    public ResponseEntity<Post>




}
