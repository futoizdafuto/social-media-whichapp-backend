package com.example.Social_Media_WhichApp.controller;


import com.example.Social_Media_WhichApp.entity.Notification;
import com.example.Social_Media_WhichApp.repository.NotificationRepository;
import com.example.Social_Media_WhichApp.entity.Auth;
import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.repository.AuthRepository;
import com.example.Social_Media_WhichApp.repository.UserRepository;
import com.example.Social_Media_WhichApp.security.JwtUtil;
import com.example.Social_Media_WhichApp.services.AuthService;
import com.example.Social_Media_WhichApp.services.FileStorageService;
import com.example.Social_Media_WhichApp.services.MailService;
import com.example.Social_Media_WhichApp.services.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin(origins = "http://localhost:8443")
@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthService authService;
    @Autowired
    private MailService mailService;
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private FileStorageService fileStorageService;
    InetAddress ip;

    {
        try {
            ip = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    String ipAddress = ip.toString();

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUser();
    }

    // Phương thức để xử lý đăng nhập của người dùng
    @PostMapping("/login") // Xử lý yêu cầu POST đến api/users/login
    public Map<String, Object> loginUser(@RequestParam String username, @RequestParam String password) {
        System.out.println("Request received!");
        return userService.loginUser(username, password);
    }

    // Phương thức để xử lý đăng ký của người dùng
    @PostMapping("/register") // Xử lý yêu cầu POST đến api/users/register
    public Map<String, Object> registerUser(@RequestParam String username, @RequestParam String password, @RequestParam String email, @RequestParam String name) {
        return userService.register(username, password, email, name);
    }
    @PostMapping("/logout") // Xử lý yêu cầu POST đến api/users/logout
    public Map<String, Object> logoutUser(@RequestParam  String token) {
        return userService.logoutUser(token); // Gọi phương thức dịch vụ và trả về phản hồi
    }


    // Kiểm tra trạng thái đăng nhập của người dùng
    @GetMapping("/checkLogin")
    public Map<String, Object> checkUserLoginStatus(@RequestParam String token) {
        return userService.checkUserLoginStatus(token);
    }

    // Phương thức để đăng nhập lại bằng token
    @PostMapping("/reLogin")
    public Map<String, Object> reLogin(@RequestParam String token) throws Exception {
        return userService.reLogin(token);
    }

    @PostMapping("/verify_otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Email and OTP are required"
            ));
        }

        try {
            Map<String, Object> response = userService.verifyOtpRegister(email, otp);
            if ("success".equals(response.get("status"))) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
            }
        } catch (Exception e) {
            // Log lỗi để debug
            System.err.println("OTP verification error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Server error when verifying OTP"
            ));
        }
    }

    // Phương thức để set private cho tài khoản
    @PostMapping("/updatePrivate")
    public ResponseEntity<Map<String, Object>> updatePrivate(@RequestParam String username) {
        Map<String, Object> response = userService.updatePrivate(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Phương thức để set public cho tài khoản
    @PostMapping("/updatePublic")
    public ResponseEntity<Map<String, Object>> updatePublic(@RequestParam String username) {
        Map<String, Object> response = userService.updatePublic(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getUserStatus(@RequestParam String username) {
        Map<String, Object> response = userService.getUserStatus(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<?> forgotPassword(@RequestParam Map<String, String> request) {
        String email = request.get("email");
        return userService.handleForgotPassword(email);
    }

    @PostMapping("/verify_otp_forgot_password")
    public ResponseEntity<?> verifyOtps(@RequestParam Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        return userService.handleVerifyOtp(email, otp);
    }
    @PostMapping("/update_password")
    public ResponseEntity<Map<String, Object>> updatePass(@RequestParam Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        // Gọi service để xử lý logic cập nhật password
        Map<String, Object> response = userService.updatePassword(email, password);

        // Tạo ResponseEntity từ Map
        if ("success".equals(response.get("status"))) {
            return ResponseEntity.ok(response); // Trả về 200 OK nếu thành công
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // Trả về 401 Unauthorized nếu lỗi
        }
    }
      // Endpoint để lấy thông báo của người dùng
    @GetMapping("/{userId}/notifications")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        return ResponseEntity.ok(notifications);
    }
    @PostMapping("/update_information")
    public ResponseEntity<Map<String, Object>> updateInformation(@RequestParam Long userId,
                                                                 @RequestParam(required = false) String gender,
                                                                 @RequestParam(required = false) String birthDate,
                                                                 @RequestParam(required = false) MultipartFile avatarFile) {
        // Lấy thông tin người dùng theo userId
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            // Trả về lỗi nếu user không tồn tại
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", "User not found"
            ));
        }

        // Cập nhật các thông tin của user nếu được truyền vào
        if (gender != null) {
            user.setGender(gender);
        }

        if (birthDate != null) {
            try {
                user.setBirthday(LocalDate.parse(birthDate));  // Chuyển đổi ngày sinh từ string sang LocalDate
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "status", "error",
                        "message", "Invalid date format. Please use yyyy-MM-dd."
                ));
            }
        }

        // Cập nhật avatar nếu có file avatarFile
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                // Lưu file avatar và lấy URL (hoặc đường dẫn lưu trữ)
                String fileName = fileStorageService.save_File(avatarFile);  // Lưu file
                String fileType = fileName.endsWith(".mp4") ? "video" : "image"; // Xác định kiểu file (có thể bỏ qua nếu chỉ lưu ảnh)

                // Tạo URL của avatar
                String fileUrl = "https://" + ipAddress + ":8443" + "/uploads/" + getSubRandom();  // Tạo URL với địa chỉ IP và đường dẫn
                System.out.println(fileUrl);
                if (fileUrl != null) {
                    user.setAvatar_url(fileUrl);  // Cập nhật URL avatar cho user
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                            "status", "error",
                            "message", "Failed to generate avatar URL"
                    ));
                }
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "status", "error",
                        "message", "Failed to upload avatar: " + e.getMessage()
                ));
            }
        }else {
            // Nếu không upload file avatar, đặt avatar mặc định (nếu cần)
            if (user.getAvatar_url() == null || user.getAvatar_url().isEmpty()) {
                user.setAvatar_url("https://" + ipAddress + ":8443" + "/uploads/noneavatar.jpg"); // Link ảnh mặc định
            }
        }

        // Lưu thông tin đã cập nhật vào database
        userRepository.save(user);

        // Tạo phản hồi trả về
        Map<String, Object> response = Map.of(
                "updateInformationUser", Map.of(
                        "status", "success",
                        "user", Map.of(
                                "id", user.getUserId(),
                                "name", user.getName(),
                                "email", user.getEmail(),
                                "role", user.getRole(),
                                "username", user.getUsername(),
                                "gender", user.getGender(),
                                "birthDate", user.getBirthday(),
                                "avatar", user.getAvatar_url()
                        )
                )
        );

        return ResponseEntity.ok(response);
    }

    private String getSubRandom() {
        return fileStorageService.provider_RandomString();  // Đảm bảo hàm này không trả về null
    }

}
