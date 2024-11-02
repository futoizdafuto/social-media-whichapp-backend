package com.example.Social_Media_WhichApp.services;

import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public User createUser(User user){

        return userRepository.save(user);
    }
    public User findUserById(long id){
        User user = userRepository.findById(id).orElse(null);
        return user;
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    // Đăng nhập bằng username
    public Map<String, Object> loginUser(String username, String password) {
        User user = findUserByUsername(username); // Tìm người dùng theo tên người dùng (username)
        Map<String, Object> response = new HashMap<>(); // Tạo một bản đồ (map) để lưu trữ phản hồi

        // Kiểm tra xem người dùng có tồn tại và mật khẩu có khớp không
        if (user != null && user.getPassword().equals(password)) {
            response.put("login", true); // Nếu đăng nhập thành công, thêm thông tin vào bản đồ
            response.put("status", "success");
            response.put("message", "Login successful");
            // Tạo bản đồ cho dữ liệu người dùng
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getUser_id());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("name", user.getName());
            userData.put("role", user.getRole().getRole_id());
            userData.put("avatar", user.getAvatar_url());

            response.put("data", Map.of("user", userData)); // Thêm thông tin người dùng vào json
            response.put("time", LocalDateTime.now());
        } else {
            response.put("login", false); // Nếu đăng nhập không thành công, đánh dấu là false
            response.put("status", "error");
            response.put("message", "Login error");
        }

        return response; // Trả về phản hồi
    }

}
