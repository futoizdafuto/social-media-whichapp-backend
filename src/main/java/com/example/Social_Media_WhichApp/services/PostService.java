package com.example.Social_Media_WhichApp.services;

//import com.example.Social_Media_WhichApp.controller.NotificationController;
import com.example.Social_Media_WhichApp.entity.*;
import com.example.Social_Media_WhichApp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private PostLikeRepository postLikeRepository;
    private PostCommentRepository postCommentRepository;
    private NotificationRepository notificationRepository;

//    @Autowired
//    private NotificationController notificationController;

    @Autowired
    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       PostLikeRepository postLikeRepository,
                       PostCommentRepository postCommentRepository,
                       NotificationRepository notificationRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postLikeRepository = postLikeRepository;
        this.postCommentRepository = postCommentRepository;
        this.notificationRepository = notificationRepository;
    }

    public PostService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public PostService() {

    }

    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    public Post getPostById(Long id){
        return  postRepository.findById(id).orElse(null);
    }
    public void deletePostById(Long id){
        postRepository.deleteById(id);
    }
    public List<Post> getPostsByUsername(String username) {
        return postRepository.findByUser_Username(username);
    }

    public String likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (post == null || user == null) {
            return "Post or User not found.";
        }

        // Kiểm tra xem user đã like post này chưa
        Optional<PostLike> existingLike = postLikeRepository.findByPostAndUser(post, user);
        if (existingLike.isPresent()) {
            return "You have already liked this post.";
        }

        // Tạo một "like" mới và lưu vào database
        PostLike postLike = new PostLike();
        postLike.setPost(post);
        postLike.setUser(user);
        postLikeRepository.save(postLike);

        // Tăng likeCount
        post.setLike_count(post.getLike_count() + 1);
        postRepository.save(post);

        // Tạo thông báo cho chủ sở hữu bài viết
        createNotification(post.getUser(), post, user.getUsername() + "\n" + user.getUsername() + " liked your post.");

        return "Post liked successfully.";
    }

    public String unlikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (post == null || user == null) {
            return "Post or User not found.";
        }

        // Kiểm tra xem user đã like post này chưa
        Optional<PostLike> existingLike = postLikeRepository.findByPostAndUser(post, user);
        if (!existingLike.isPresent()) {
            return "You haven't liked this post.";
        }

        // Xóa like khỏi database
        postLikeRepository.delete(existingLike.get());

        // Giảm số lượt like
        if (post.getLike_count() > 0) {
            post.setLike_count(post.getLike_count() - 1);
        }
        postRepository.save(post);

        return "Post unliked successfully.";
    }

    public boolean checkLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        // Kiểm tra xem user đã like post này chưa
        Optional<PostLike> existingLike = postLikeRepository.findByPostAndUser(post, user);
        if (!existingLike.isPresent()) {
            return false;
        }
        return true;
    }

    public long getLikeCount(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        long count = 0;
        if(post != null) {count = post.getLike_count();}
        return count;
    }

    // Thêm bình luận mới cho một bài viết
    public String addComment(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (post == null || user == null) {
            return "Post or User not found.";
        }

        PostComment postComment = new PostComment();
        postComment.setPost(post);
        postComment.setUser(user);
        postComment.setContent(content);
        postComment.setCreatedAt(new Date());

        postCommentRepository.save(postComment);

        // Tăng commentCount
        post.setComment_count(post.getComment_count() + 1);
        postRepository.save(post);

        // Tạo thông báo cho chủ sở hữu bài viết
        createNotification(post.getUser(), post,  user.getUsername() + "\n" + user.getUsername() + " commented on your post.");

        return "Comment added successfully.";
    }

    // Lấy tất cả bình luận của một bài viết
    public List<PostComment> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null) {
            return null;
        }

        return postCommentRepository.findByPost(post);
    }

     void createNotification(User user,Post post, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setPost(post);
        notification.setMessage(message);
        notification.setIsread(false);
        notification.setCreatedAt(new Date());
        notificationRepository.save(notification);

        // Gửi thông báo qua WebSocket
        //notificationController.sendNotification(user.getUsername(), message);
    }
// thêm phương thức để tạo thông báo không cần đưa post
    void createNotification(User user, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setPost(null);
        notification.setMessage(message);
        notification.setIsread(false);
        notification.setCreatedAt(new Date());
        notificationRepository.save(notification);

        // Gửi thông báo qua WebSocket
        //notificationController.sendNotification(user.getUsername(), message);
    }

}
