package com.example.Social_Media_WhichApp.controller;


import com.example.Social_Media_WhichApp.entity.Media;
import com.example.Social_Media_WhichApp.entity.Post;
import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.repository.PostRepository;
import com.example.Social_Media_WhichApp.repository.UserRepository;
import com.example.Social_Media_WhichApp.services.FileStorageService;
import com.example.Social_Media_WhichApp.services.PostService;
import com.example.Social_Media_WhichApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;



//    @GetMapping
//    public List<Post> getAllPosts(){
//        return postService.getAllPosts();
//    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();

        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Post post : posts) {
            Map<String, Object> postResponse = new HashMap<>();
            postResponse.put("post_id", post.getPost_id());
            postResponse.put("content", post.getContent());
            postResponse.put("mediaList", post.getMediaList());
            postResponse.put("created_at", post.getCreated_at());

            // Thêm thông tin User vào response
            if (post.getUser() != null) {
                Map<String, Object> userResponse = new HashMap<>();
                userResponse.put("user_id", post.getUser().getUser_id());
                userResponse.put("username", post.getUser().getUsername());
                postResponse.put("user", userResponse);
            }

            responseList.add(postResponse);
        }

        return new ResponseEntity<>(responseList, HttpStatus.OK);
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
            post.setUser(userService.findUserById(user_id));  // Gán User vào Post
            post.setContent(content);
            post.setCreated_at(new Date());

            List<Media> mediaList = new ArrayList<>();

            for (MultipartFile file : files) {
                String fileName =  fileStorageService.save_File(file);
                String fileType = fileName.endsWith(".mp4") ? "video" : "image";

                String fileUrl = "https:/"+ ipAddress + ":8443"+"/uploads/" + getSubRandom();


                Media media = new Media(fileUrl, fileType , post);
                mediaList.add(media);
            }
            post.setMediaList(mediaList);

            // lưu post vào cơ sở dữ liệu
            Post savePost = postRepository.save(post);

            // Thêm thông tin của user vào phản hồi
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("user_id", post.getUser().getUser_id());
            userResponse.put("username", post.getUser().getUsername());  // Nếu có thuộc tính này

            response.put("message", "Tạo bài viết thành công");
            response.put("post", savePost);
            response.put("user", userResponse);  // Thêm thông tin user vào phản hồi

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            response.put("error", "Failed to upload post: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        try {
            // Kiểm tra post tồn tại
            Post post = postService.getPostById(postId);
            if (post == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy bài viết");
            }

            // Lấy thông tin user hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Người dùng không được xác thực");
            }

            // Kiểm tra xem có phải chủ post không
            User currentUser = userService.findUserByUsername(authentication.getName());
            if (!currentUser.getUser_id().equals(post.getUser().getUser_id())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Bạn chỉ có thể xóa khi bạn là chủ bài viết");
            }

            // Xóa các file media
            if (post.getMediaList() != null && !post.getMediaList().isEmpty()) {
                for (Media media : post.getMediaList()) {
                    try {
                        String mediaUrl = media.getUrl();
                        // Tách lấy phần tên file từ URL
                        String fileName = mediaUrl.substring(mediaUrl.lastIndexOf("/") + 1);

                        // Tạo đường dẫn đầy đủ đến file
                        Path mediaPath = Paths.get("src", "main", "resources", "static", "uploads", fileName);


                        System.out.println("Trying to delete file at: " + mediaPath.toString());

                        if (Files.exists(mediaPath)) {
                            Files.delete(mediaPath);
                            System.out.println("Successfully deleted file: " + mediaPath);
                        } else {
                            System.out.println("File not found: " + mediaPath);
                        }

                    } catch (Exception e) {
                        System.err.println("Error deleting media file: " + e.getMessage());
                        e.printStackTrace(); // In ra stack trace để debug
                    }
                }
            }

            // Xóa post
            postService.deletePostById(postId);
            return ResponseEntity.ok("Xóa bài viết thành công");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa bài viết: " + e.getMessage());
        }
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<String> updatePostContent(@PathVariable Long postId,
                                                    @RequestParam String content) {
        try {
            // Kiểm tra post tồn tại
            Post post = postService.getPostById(postId);
            if (post == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Post not found");
            }

            // Cập nhật content
            post.setContent(content);

            // Lưu vào database
            postRepository.save(post);

            return ResponseEntity.ok("Post content updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating post: " + e.getMessage());
        }
    }
}
