package com.example.Social_Media_WhichApp.controller;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

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
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadPost(
            @RequestParam("user_id") Long user_id,
            @RequestParam("content") String content,
            @RequestParam("file")MultipartFile file
            ){
        Map<String, Object> response = new HashMap<>();
        try {
            // luu file anh
            String fileName = fileStorageService.save_File(file);
            String imgURL = "/static/uploads/" + fileName;

            // Tao va luu bai viet vao DB
            Post post = new Post();
            post.setUser(new User(user_id));
            post.setContent(content);
            post.setImg_url(imgURL);
            post.setCreated_at(new Date());

            System.out.println(postRepository.save(post));

            response.put("message", "Post created successfully");
            response.put("post", post);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            response.put("error", "Failed to upload post: "+e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
