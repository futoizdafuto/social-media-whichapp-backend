package com.example.Social_Media_WhichApp.services;

import com.example.Social_Media_WhichApp.entity.Role;
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


    public User findUserById(long id){
        User user = userRepository.findById(id).orElse(null);
        return user;
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public User createUser(User user){
        User newUser = userRepository.save(user);
        return userRepository.save(user);
    }

    // Đăng ký tài khoản
    public Map<String, Object> register(String username, String password, String email, String name) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra xem tên người dùng có tồn tại không
        if (userRepository.findByUsername(username) != null) {
            response.put("register", Map.of(
            "status", "error",
            "message", "Username already exists"
            ));
            return response;
        }

        // Tạo một đối tượng Role với id = 2
        Role defaultRole = new Role();
        defaultRole.setRole_id(2L); // Đặt id vai trò mặc định là 2

        // Tạo đối tượng người dùng mới
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); // Nên mã hóa mật khẩu trước khi lưu
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setRole(defaultRole); // Thiết lập vai trò cho người dùng

        // Lưu người dùng mới vào cơ sở dữ liệu
        userRepository.save(newUser);

        // Tạo phản hồi thành công
        Map<String, Object> userData = new HashMap<>(); // Bản đồ cho thông tin người dùng
        userData.put("user_id", newUser.getUser_id());
        userData.put("username", newUser.getUsername());
        userData.put("email", newUser.getEmail());
        userData.put("name", newUser.getName());
        userData.put("role", newUser.getRole().getRole_id());

        // Cập nhật phản hồi
        response.put("register", Map.of(
                "status", "success",
                "message", "User registered successfully",
                "data", Map.of("user", userData),
                "time", LocalDateTime.now()
        ));

        return response; // Trả về phản hồi
    }

    // Đăng nhập bằng username
    public Map<String, Object> loginUser(String username, String password) {
        User user = findUserByUsername(username); // Tìm người dùng theo tên người dùng (username)
        Map<String, Object> response = new HashMap<>(); // Tạo một bản đồ (map) để lưu trữ phản hồi

        // Kiểm tra xem người dùng có tồn tại và mật khẩu có khớp không
        if (user != null && user.getPassword().equals(password)) {
            // Nếu đăng nhập thành công
            Map<String, Object> userData = new HashMap<>(); // Tạo bản đồ cho dữ liệu người dùng
            userData.put("id", user.getUser_id());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("name", user.getName());
            userData.put("role", user.getRole().getRole_id());
            userData.put("avatar", user.getAvatar_url());

            // Thêm thông tin vào phản hồi
            response.put("login", Map.of(
                    "status", "success",
                    "message", "Login successful",
                    "data", Map.of("user", userData),
                    "time", LocalDateTime.now()
            ));
        } else {
            // Nếu đăng nhập không thành công
            response.put("login", Map.of(
                    "status", "error",
                    "message", "Login error"
            ));
        }

        return response; // Trả về phản hồi
    }


}
