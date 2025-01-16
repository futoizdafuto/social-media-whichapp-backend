package com.example.Social_Media_WhichApp.controller;


import com.example.Social_Media_WhichApp.entity.Media;
import com.example.Social_Media_WhichApp.entity.Post;
import com.example.Social_Media_WhichApp.entity.PostComment;
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
            postResponse.put("like_count", post.getLike_count());
            postResponse.put("comment_count", post.getComment_count());

            // Thêm thông tin User vào response
            if (post.getUser() != null) {
                Map<String, Object> userResponse = new HashMap<>();
                userResponse.put("user_id", post.getUser().getUserId());
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
            userResponse.put("user_id", post.getUser().getUserId());
            userResponse.put("username", post.getUser().getUsername());  // Nếu có thuộc tính này

            response.put("message", "Post created successfully");
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
                        .body("Post not found");
            }

            // Lấy thông tin user hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }

            // Kiểm tra xem có phải chủ post không
            User currentUser = userService.findUserByUsername(authentication.getName());
            if (!currentUser.getUserId().equals(post.getUser().getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You can only delete your own posts");
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
            return ResponseEntity.ok("Post and associated media deleted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting post: " + e.getMessage());
        }
    }
    @GetMapping("/by-user")
    public ResponseEntity<List<Map<String, Object>>> getPostsByUsername(@RequestParam String username) {
        // Lấy tất cả các bài post của người dùng theo username
        List<Post> posts = postService.getPostsByUsername(username);

        // Kiểm tra nếu không có bài post nào của người dùng
        if (posts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonList(Collections.singletonMap("message", "No posts found for this user")));
        }

        // Tạo danh sách phản hồi
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
                userResponse.put("user_id", post.getUser().getUserId());
                userResponse.put("username", post.getUser().getUsername());
                postResponse.put("user", userResponse);
            }

            responseList.add(postResponse);
        }

        // Trả về danh sách bài post của người dùng
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
    @GetMapping("/sizeby-user")
    public ResponseEntity<Map<String, Object>> getPostsCountByUsername(@RequestParam String username) {
        // Lấy tất cả các bài post của người dùng theo username
        List<Post> posts = postService.getPostsByUsername(username);

        // Tạo một Map để chứa dữ liệu phản hồi
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra nếu không có bài post nào của người dùng
        if (posts.isEmpty()) {
            response.put("status", "error");
            response.put("message", "No posts found for this user");
            response.put("post_count", 0);  // Trả về số lượng bài viết là 0 nếu không có bài post nào
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Trả về số lượng bài viết
        response.put("status", "success");
        response.put("post_count", posts.size());  // Sử dụng posts.size() để lấy số lượng bài viết

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/image")
    public ResponseEntity<List<Map<String, Object>>> getImagesByUsername(@RequestParam String username) {
        // Lấy tất cả các bài post của người dùng theo username
        List<Post> posts = postService.getPostsByUsername(username);

        // Kiểm tra nếu không có bài post nào của người dùng
        if (posts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonList(Collections.singletonMap("message", "No posts found for this user")));
        }

        // Tạo danh sách để lưu trữ các ảnh của người dùng
        List<Map<String, Object>> imageList = new ArrayList<>();

        // Duyệt qua tất cả các bài viết để lấy URL ảnh
        for (Post post : posts) {
            // Lấy danh sách media của bài viết
            List<Media> mediaList = post.getMediaList();

            if (mediaList != null && !mediaList.isEmpty()) {
                for (Media media : mediaList) {
                    if ("image".equals(media.getType())) { // Chỉ lấy ảnh
                        Map<String, Object> imageResponse = new HashMap<>();
                        imageResponse.put("image_url", media.getUrl());
                        imageResponse.put("created_at", post.getCreated_at());
                        imageList.add(imageResponse);
                    }
                }
            }
        }

        // Sắp xếp danh sách ảnh theo thứ tự created_at từ mới nhất đến cũ nhất
        imageList.sort((a, b) -> ((Date) b.get("created_at")).compareTo((Date) a.get("created_at")));

        // Trả về danh sách các ảnh đã sắp xếp
        return new ResponseEntity<>(imageList, HttpStatus.OK);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> likePost(@PathVariable Long postId, @RequestBody Map<String, Object> body) {
        Long user_id = Long.valueOf(body.get("user_id").toString());
        String message = postService.likePost(postId, user_id);
        // Trả về JSON thay vì chuỗi thuần
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", message);
        responseBody.put("like_count", postService.getLikeCount(postId)); // Lấy số lượng like hiện tại

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/{postId}/unlike")
    public ResponseEntity<Map<String, Object>> unlikePost(@PathVariable Long postId, @RequestBody Map<String, Object> body) {
        Long user_id = Long.valueOf(body.get("user_id").toString());
        String message = postService.unlikePost(postId, user_id);

        // Trả về JSON thay vì chuỗi thuần
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", message);
        responseBody.put("like_count", postService.getLikeCount(postId)); // Lấy số lượng like hiện tại

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/{postId}/checkLike")
    public ResponseEntity<Map<String, Object>> checkLikePost(@PathVariable Long postId, @RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("user_id").toString());

        // Gọi service để kiểm tra trạng thái like
        boolean isLiked = postService.checkLike(postId, userId);

        // Chuẩn bị JSON trả về
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("isLiked", isLiked);
        System.out.println("Check Like Status: " + isLiked);
        return ResponseEntity.ok(responseBody);
    }


    // Endpoint để thêm bình luận vào bài viết
    @PostMapping("/{postId}/comments")
    public ResponseEntity<String> addComment(
            @PathVariable Long postId,
            @RequestParam Long user_id,
            @RequestParam String content) {
        String response = postService.addComment(postId, user_id, content);
        return ResponseEntity.ok(response);
    }

    // Endpoint để lấy tất cả bình luận của một bài viết
    @GetMapping("/{postId}/allcomments")
    public ResponseEntity<List<PostComment>> getCommentsByPost(@PathVariable Long postId) {
        List<PostComment> comments = postService.getCommentsByPost(postId);

        if (comments == null) {
            return ResponseEntity.notFound().build();
        }

        // Log các bình luận để kiểm tra
        comments.forEach(comment -> {
            System.out.println("Comment: " + comment.getContent());
        });

        return ResponseEntity.ok(comments);
    }

}
