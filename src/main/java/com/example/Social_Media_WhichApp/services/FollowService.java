package com.example.Social_Media_WhichApp.services;

import com.example.Social_Media_WhichApp.entity.Follow;
import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.repository.FollowRepository;
import com.example.Social_Media_WhichApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FollowService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserService userService; // Used to check login status

    // Kiểm tra xem người dùng có tồn tại không
    private boolean checkUserExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    // Kiểm tra xem người dùng có đăng nhập không (sử dụng token)
    private boolean isUserLoggedIn(String token) {
        return userService.checkUserLoginStatus(token).get("status").equals("success");
    }

    // Theo dõi một người dùng (single user follow)
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
            response.put("message", "User " + targetUsername + " does not exist.");
            return response;
        }

        User follower = userRepository.findByUsername(username);
        User followed = userRepository.findByUsername(targetUsername);

        // Kiểm tra xem người dùng đã theo dõi chưa
        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowed(follower, followed);
        if (existingFollow.isPresent()) {
            response.put("status", "error");
            response.put("message", "You are already following " + targetUsername + ".");
            return response;
        }

        // Thêm quan hệ follow mới vào cơ sở dữ liệu
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowed(followed);
        followRepository.save(follow);

        response.put("status", "success");
        response.put("message", "You are now following " + targetUsername);
        return response;
    }

    // Theo dõi nhiều người dùng (multiple user follow)
    public Map<String, Object> followMultipleUsers(String username, List<String> targetUsernames, String token) {
        Map<String, Object> response = new HashMap<>();
        List<String> successMessages = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        // Kiểm tra xem người dùng có đăng nhập không
        if (!isUserLoggedIn(token)) {
            response.put("status", "error");
            response.put("message", "You need to log in to follow other users.");
            return response;
        }

        // Kiểm tra xem người dùng có tồn tại không
        if (!checkUserExists(username)) {
            response.put("status", "error");
            response.put("message", "User " + username + " does not exist.");
            return response;
        }

        User follower = userRepository.findByUsername(username);

        // Lặp qua từng targetUsername và thực hiện hành động theo dõi
        for (String targetUsername : targetUsernames) {
            if (!checkUserExists(targetUsername)) {
                // Người dùng không tồn tại
                errorMessages.add("User " + targetUsername + " does not exist.");
                continue;
            }

            User followed = userRepository.findByUsername(targetUsername);

            // Kiểm tra xem người dùng đã follow chưa
            Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowed(follower, followed);
            if (existingFollow.isPresent()) {
                // Người dùng đã follow rồi
                errorMessages.add("You are already following " + targetUsername + ".");
                continue;
            }

            // Thêm quan hệ follow mới vào cơ sở dữ liệu
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowed(followed);
            followRepository.save(follow);

            successMessages.add("You are now following " + targetUsername);
        }

        // Xử lý kết quả trả về
        if (successMessages.isEmpty() && errorMessages.isEmpty()) {
            response.put("status", "error");
            response.put("message", "No valid users to follow.");
        } else {
            response.put("status", "success");
            response.put("message", "Follow attempt completed.");
            if (!successMessages.isEmpty()) {
                response.put("success_messages", successMessages);
            }
            if (!errorMessages.isEmpty()) {
                response.put("error_messages", errorMessages);
            }
        }

        return response;
    }

    // Bỏ theo dõi một người dùng (single user unfollow)
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
            response.put("message", "User " + targetUsername + " does not exist.");
            return response;
        }

        User follower = userRepository.findByUsername(username);
        User followed = userRepository.findByUsername(targetUsername);

        // Kiểm tra xem người dùng có đang theo dõi người này không
        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowed(follower, followed);
        if (existingFollow.isEmpty()) {
            response.put("status", "error");
            response.put("message", "You are not following " + targetUsername + ".");
            return response;
        }

        // Xóa quan hệ follow khỏi cơ sở dữ liệu
        followRepository.delete(existingFollow.get());

        response.put("status", "success");
        response.put("message", "You have unfollowed " + targetUsername);
        return response;
    }

    // Bỏ theo dõi nhiều người dùng (multiple user unfollow)
    public Map<String, Object> unfollowMultipleUsers(String username, List<String> targetUsernames, String token) {
        Map<String, Object> response = new HashMap<>();
        List<String> successMessages = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        // Kiểm tra xem người dùng có đăng nhập không
        if (!isUserLoggedIn(token)) {
            response.put("status", "error");
            response.put("message", "You need to log in to unfollow users.");
            return response;
        }

        // Kiểm tra xem người dùng có tồn tại không
        if (!checkUserExists(username)) {
            response.put("status", "error");
            response.put("message", "User " + username + " does not exist.");
            return response;
        }

        User follower = userRepository.findByUsername(username);

        // Lặp qua từng targetUsername và thực hiện hành động unfollow
        for (String targetUsername : targetUsernames) {
            if (!checkUserExists(targetUsername)) {
                // Người dùng không tồn tại
                errorMessages.add("User " + targetUsername + " does not exist.");
                continue;
            }

            User followed = userRepository.findByUsername(targetUsername);

            // Kiểm tra xem người dùng có đang theo dõi người này không
            Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowed(follower, followed);
            if (existingFollow.isEmpty()) {
                // Người dùng không theo dõi rồi
                errorMessages.add("You are not following " + targetUsername + ".");
                continue;
            }

            // Xóa quan hệ follow khỏi cơ sở dữ liệu
            followRepository.delete(existingFollow.get());

            successMessages.add("You have unfollowed " + targetUsername);
        }

        // Xử lý kết quả trả về
        if (successMessages.isEmpty() && errorMessages.isEmpty()) {
            response.put("status", "error");
            response.put("message", "No valid users to unfollow.");
        } else {
            response.put("status", "success");
            response.put("message", "Unfollow attempt completed.");
            if (!successMessages.isEmpty()) {
                response.put("success_messages", successMessages);
            }
            if (!errorMessages.isEmpty()) {
                response.put("error_messages", errorMessages);
            }
        }

        return response;
    }

    // Lấy danh sách những người mà người dùng đang follow và số lượng người đã được follow
    public Map<String, Object> getFollows(String username) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra xem người dùng có tồn tại không
        if (!checkUserExists(username)) {
            response.put("status", "error");
            response.put("message", "User " + username + " does not exist.");
            return response;
        }

        User user = userRepository.findByUsername(username);

        // Lấy tất cả người dùng mà user này đang follow
        List<Follow> follows = followRepository.findByFollower(user);

        // Chuyển danh sách Follow thành danh sách tên người dùng được follow
        List<String> followedUsernames = follows.stream()
                .map(follow -> follow.getFollowed().getUsername())
                .collect(Collectors.toList());

        response.put("status", "success");
        response.put("message", "Fetched followed users successfully.");
        response.put("data", followedUsernames);
        response.put("follow_count", followedUsernames.size());  // Return the follow count
        return response;
    }
}
