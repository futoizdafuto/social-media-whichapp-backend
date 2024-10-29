package com.example.Social_Media_WhichApp.controller;

import com.example.Social_Media_WhichApp.entity.Media;
import com.example.Social_Media_WhichApp.entity.Post;
import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.repository.PostRepository;
import com.example.Social_Media_WhichApp.service.FileStorageService;
import com.example.Social_Media_WhichApp.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("api/posts")
public class PostController {

    @Autowired
    public PostService postService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

//    @PostMapping
//    public Post createPost(@RequestBody Post post) {
//        return postService.createPost(post);
//    }

    @Autowired
    private PostRepository postRepository;

    // API xóa bài viết theo ID
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deletePost(@PathVariable Long id) {
//        if (postRepository.existsById(id)) {
//            postRepository.deleteById(id);
//            return ResponseEntity.ok("Post deleted successfully.");
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found.");
//        }
//    }


    // API upload bài post với nhiều file (ảnh/video)
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadPost(
            @RequestParam("user_id") Long userId,
            @RequestParam("content") String content,
            @RequestParam("files") List<MultipartFile> files) {

        Map<String, Object> response = new HashMap<>();
        try {
            // Khởi tạo bài viết
            Post post = new Post();
            post.setUser(new User(userId)); // Gắn user cho post
            post.setContent(content);
            post.setCreated_at(new Date());

            List<Media> mediaList = new ArrayList<>();

            // Lưu từng file và tạo đối tượng Media
            for (MultipartFile file : files) {
                String fileName = fileStorageService.save_File(file);
                String fileType = fileName.endsWith(".mp4") ? "video" : "image";
                String fileUrl = "/uploads/" + fileName;

                Media media = new Media(fileUrl, fileType, post);
                mediaList.add(media);
            }

            post.setMediaList(mediaList); // Liên kết media với post

            // Lưu post vào cơ sở dữ liệu
            Post savedPost = postRepository.save(post);
            response.put("message", "Post created successfully");
            response.put("post", savedPost);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IOException e) {
            response.put("error", "Failed to upload post: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
