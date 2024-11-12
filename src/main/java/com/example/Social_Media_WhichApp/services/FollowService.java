package com.example.Social_Media_WhichApp.services;

import com.example.Social_Media_WhichApp.entity.Follow;
import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.repository.FollowRepository;
import com.example.Social_Media_WhichApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@Service

public class FollowService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserService userService; // Dùng UserService để kiểm tra trạng thái đăng nhập

    // Kiểm tra xem người dùng có tồn tại không
    private boolean checkUserExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    // Kiểm tra xem người dùng có đăng nhập không (sử dụng token)
    private boolean isUserLoggedIn(String token) {
        return userService.checkUserLoginStatus(token).get("status").equals("success");
    }

    // Theo dõi người dùng (Follow)
    public Map<String, Object> followUser(String username, String targetUsername, String token) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra xem người dùng có đăng nhập không
        if (!isUserLoggedIn(token)) {
            response.put("status", "error");
            response.put("message", "You need to log in to follow other users.");
            return response;
        }

        // Kiểm tra xem người dùng có tồn tại không
        if (!checkUserExists(targetUsername)) {
            response.put("status", "error");
            response.put("message", "User does not exist.");
            return response;
        }

        // Kiểm tra xem người dùng đã theo dõi người này chưa
        User follower = userRepository.findByUsername(username);
        User followed = userRepository.findByUsername(targetUsername);

        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowed(follower, followed);
        if (existingFollow.isPresent()) {
            response.put("status", "error");
            response.put("message", "You are already following this user.");
            return response;
        }

        // Thêm quan hệ theo dõi vào cơ sở dữ liệu
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowed(followed);
        followRepository.save(follow);

        response.put("status", "success");
        response.put("message", "You are now following " + targetUsername);
        return response;
    }

    // Bỏ theo dõi người dùng (Unfollow)
    public Map<String, Object> unfollowUser(String username, String targetUsername, String token) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra xem người dùng có đăng nhập không
        if (!isUserLoggedIn(token)) {
            response.put("status", "error");
            response.put("message", "You need to log in to unfollow users.");
            return response;
        }

        // Kiểm tra xem người dùng có tồn tại không
        if (!checkUserExists(targetUsername)) {
            response.put("status", "error");
            response.put("message", "User does not exist.");
            return response;
        }

        // Kiểm tra xem người dùng có đang theo dõi người này không
        User follower = userRepository.findByUsername(username);
        User followed = userRepository.findByUsername(targetUsername);

        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowed(follower, followed);
        if (existingFollow.isEmpty()) {
            response.put("status", "error");
            response.put("message", "You are not following this user.");
            return response;
        }

        // Xóa quan hệ theo dõi khỏi cơ sở dữ liệu
        followRepository.delete(existingFollow.get());

        response.put("status", "success");
        response.put("message", "You have unfollowed " + targetUsername);
        return response;
    }
}
