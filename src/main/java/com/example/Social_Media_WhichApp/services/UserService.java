package com.example.Social_Media_WhichApp.services;

import com.example.Social_Media_WhichApp.entity.Role;
import com.example.Social_Media_WhichApp.entity.Token;
import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.repository.TokenRepository;
import com.example.Social_Media_WhichApp.repository.UserRepository;
import com.example.Social_Media_WhichApp.security.EncryptPassword;
import com.example.Social_Media_WhichApp.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenRepository tokenRepository;

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
        try {
            newUser.setPassword(EncryptPassword.encrypt(password)); // Mã hóa mật khẩu trước khi lưu
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Encryption error");
            return response;
        }

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
    public Map<String, Object> loginUser(String username, String password, String deviceId) {
        User user = findUserByUsername(username); // Tìm người dùng theo tên người dùng (username)
        Map<String, Object> response = new HashMap<>(); // Tạo một bản đồ (map) để lưu trữ phản hồi

        // Kiểm tra xem người dùng có tồn tại
        if (user != null) {
            try {
                // Giải mã mật khẩu đã lưu và kiểm tra xem có khớp với mật khẩu nhập vào không
                String decryptedPassword = EncryptPassword.decrypt(user.getPassword());
                if (decryptedPassword.equals(password)) {
                    // Tạo token cho người dùng
                    String tokenValue = jwtUtil.generateToken(username);
                    Token token = new Token(tokenValue, LocalDateTime.now(),
                            LocalDateTime.now().plusSeconds(jwtUtil.getExpiration()), user, deviceId);
                    token.setDeviceId(deviceId);
                    tokenRepository.save(token);

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
                            "Login", true,
                            "status", "success",
                            "message", "Login successful",
                            "token", token,
                            "data", Map.of("user", userData),
                            "time", LocalDateTime.now()
                    ));
                } else {
                    // Nếu mật khẩu không khớp
                    response.put("login", Map.of(
                            "Login", false,
                            "status", "error",
                            "message", "Invalid password"
                    ));
                }
            } catch (Exception e) {
                // Nếu xảy ra lỗi giải mã
                response.put("login", Map.of(
                        "Login", false,
                        "status", "error",
                        "message", "Decryption error"
                ));
            }
        } else {
            // Nếu người dùng không tồn tại
            response.put("login", Map.of(
                    "Login", false,
                    "status", "error",
                    "message", "User not found"
            ));
        }

        return response; // Trả về phản hồi
    }

    @Transactional // Đánh dấu phương thức này là giao dịch, để đảm bảo tính nhất quán
    public void logoutUser(String username, String deviceId) {
        // Tìm kiếm người dùng dựa trên tên đăng nhập
        User user = userRepository.findByUsername(username);
        if (user != null) {
            // Nếu người dùng tồn tại, xóa tất cả các token liên quan đến người dùng đó
            tokenRepository.deleteByUserAndDeviceId(user, deviceId);
        }
    }

    // kiểm tra login lấy ra thông tin đăng nhập
    public Map<String, Object> checkUserLoginStatus(String tokenValue) {
        Map<String, Object> response = new HashMap<>();

        // Tìm token trong cơ sở dữ liệu
        Optional<Token> token = tokenRepository.findByToken(tokenValue);

        if (token.isPresent() && token.get().getExpiresAt().isAfter(LocalDateTime.now())) {
            // Token hợp lệ, lấy thông tin người dùng
            User user = token.get().getUser();
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getUser_id());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("name", user.getName());
            userData.put("role", user.getRole().getRole_id());
            userData.put("avatar", user.getAvatar_url());

            response.put("status", "success");
            response.put("message", "User is logged in");
            response.put("data", Map.of("user", userData));
        } else {
            // Token không hợp lệ hoặc hết hạn
            response.put("status", "error");
            response.put("message", "Invalid or expired token");
        }

        return response;
    }

}
