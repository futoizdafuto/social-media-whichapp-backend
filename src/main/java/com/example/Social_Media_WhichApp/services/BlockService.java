package com.example.Social_Media_WhichApp.services;

import com.example.Social_Media_WhichApp.entity.Block;
import com.example.Social_Media_WhichApp.entity.Follow;
import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.repository.BlockRepository;
import com.example.Social_Media_WhichApp.repository.FollowRepository;
import com.example.Social_Media_WhichApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlockService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlockRepository blockRepository;

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

    // Block một người dùng
    public Map<String, Object> blockUser(String username, String targetUsername, String token) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra xem người dùng có đăng nhập không
        if (!isUserLoggedIn(token)) {
            response.put("status", "error");
            response.put("message", "You need to log in to block other users.");
            return response;
        }

        // Kiểm tra xem người dùng có tồn tại không
        if (!checkUserExists(targetUsername)) {
            response.put("status", "error");
            response.put("message", "User " + targetUsername + " does not exist.");
            return response;
        }

        User blocker = userRepository.findByUsername(username);
        User blocked = userRepository.findByUsername(targetUsername);

        // Kiểm tra xem người dùng đã block chưa
        Optional<Block> existingBlock = blockRepository.findByBlockerAndBlocked(blocker, blocked);
        if (existingBlock.isPresent()) {
            response.put("status", "error");
            response.put("message", "You have already blocked " + targetUsername + ".");
            return response;
        }

        // Thêm quan hệ block mới vào cơ sở dữ liệu
        Block block = new Block();
        block.setBlocker(blocker);
        block.setBlocked(blocked);
        blockRepository.save(block);

        // Đồng thời unfollow nếu có quan hệ follow trước đó
        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowed(blocker, blocked);
        existingFollow.ifPresent(follow -> followRepository.delete(follow));

        response.put("status", "success");
        response.put("message", "You have blocked " + targetUsername);
        return response;
    }

    // Unblock một người dùng
    public Map<String, Object> unblockUser(String username, String targetUsername, String token) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra xem người dùng có đăng nhập không
        if (!isUserLoggedIn(token)) {
            response.put("status", "error");
            response.put("message", "You need to log in to unblock users.");
            return response;
        }

        // Kiểm tra xem người dùng có tồn tại không
        if (!checkUserExists(targetUsername)) {
            response.put("status", "error");
            response.put("message", "User " + targetUsername + " does not exist.");
            return response;
        }

        User blocker = userRepository.findByUsername(username);
        User blocked = userRepository.findByUsername(targetUsername);

        // Kiểm tra xem người dùng có đã block chưa
        Optional<Block> existingBlock = blockRepository.findByBlockerAndBlocked(blocker, blocked);
        if (existingBlock.isEmpty()) {
            response.put("status", "error");
            response.put("message", "You have not blocked " + targetUsername + ".");
            return response;
        }

        // Xóa quan hệ block khỏi cơ sở dữ liệu
        blockRepository.delete(existingBlock.get());

        response.put("status", "success");
        response.put("message", "You have unblocked " + targetUsername);
        return response;
    }

    // Lấy danh sách những người mà người dùng đã block
    public Map<String, Object> getBlocks(String username) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra xem người dùng có tồn tại không
        if (!checkUserExists(username)) {
            response.put("status", "error");
            response.put("message", "User " + username + " does not exist.");
            return response;
        }

        User blocker = userRepository.findByUsername(username);

        // Lấy tất cả người dùng mà user này đã block
        List<Block> blocks = blockRepository.findByBlocker(blocker);

        // Chuyển danh sách Block thành danh sách tên người dùng bị block
        List<String> blockedUsernames = blocks.stream()
                .map(block -> block.getBlocked().getUsername())
                .collect(Collectors.toList());

        response.put("status", "success");
        response.put("message", "Fetched blocked users successfully.");
        response.put("blocked_users", blockedUsernames);  // Danh sách người bị block

        return response;
    }
}
