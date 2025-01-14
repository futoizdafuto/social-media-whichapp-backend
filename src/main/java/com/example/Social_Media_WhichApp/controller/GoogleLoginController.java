package com.example.Social_Media_WhichApp.controller;

import com.example.Social_Media_WhichApp.entity.Role;
import com.example.Social_Media_WhichApp.entity.Token;
import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.repository.TokenRepository;
import com.example.Social_Media_WhichApp.repository.UserRepository;
import com.example.Social_Media_WhichApp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/users/oauth2")
public class GoogleLoginController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    public GoogleLoginController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

//    @GetMapping("/google")
//    public ResponseEntity<?> loginWithGoogle(@RequestHeader("Authorization") String accessTokenHeader) {
//        System.out.println("Request received!");
//
//        // Lấy Access Token từ header
//        String accessToken = null;
//        if (accessTokenHeader != null && accessTokenHeader.startsWith("Bearer ")) {
//            accessToken = accessTokenHeader.substring(7);
//        }
//
//        if (accessToken == null) {
//            return ResponseEntity.badRequest().body(Map.of("Login", false, "message", "Access Token is required"));
//        }
//
//        System.out.println("Access Token received: " + accessToken);
//
//        try {
//            // Gọi Google API để xác minh Access Token
//            String url = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" + accessToken;
//            RestTemplate restTemplate = new RestTemplate();
//            Map<String, Object> userInfo = restTemplate.getForObject(url, Map.class);
//
//            if (userInfo == null || !userInfo.containsKey("email")) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(Map.of("Login", false, "message", "Invalid Access Token"));
//            }
//
//            // Lấy thông tin người dùng từ phản hồi Google
//            String email = (String) userInfo.get("email");
//            String name = (String) userInfo.getOrDefault("name", "Unknown");
//            String avatarUrl = (String) userInfo.getOrDefault("picture", "Unknown");
//
//            // Kiểm tra người dùng đã tồn tại hay chưa
//            User user = userRepository.findByEmail(email).orElseGet(() -> {
//                User newUser = new User();
//                newUser.setEmail(email);
//                newUser.setName(name);
//                newUser.setAvatar_url(avatarUrl);
//                newUser.setUsername(email);
//                userRepository.save(newUser);
//                return newUser;
//            });
//
//            // Tạo JWT token
//            String jwt = jwtUtil.generateToken(user.getUsername());
//
//            return ResponseEntity.ok(Map.of(
//                    "Login", true,
//                    "token", jwt,
//                    "username", user.getUsername(),
//                    "email", user.getEmail(),
//                    "avatar_url", user.getAvatar_url()
//            ));
//        } catch (Exception e) {
//            System.err.println("Error during Google API call: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("Login", false, "message", "Error verifying Access Token: " + e.getMessage()));
//        }
//    }
@GetMapping("/google")
public ResponseEntity<?> loginWithGoogle(@RequestHeader("Authorization") String accessTokenHeader) {
    System.out.println("Request received!");

    // Lấy Access Token từ header
    String accessToken = null;
    if (accessTokenHeader != null && accessTokenHeader.startsWith("Bearer ")) {
        accessToken = accessTokenHeader.substring(7);
    }

    if (accessToken == null) {
        return ResponseEntity.badRequest().body(Map.of("Login", false, "message", "Access Token is required"));
    }

    System.out.println("Access Token received: " + accessToken);

    try {
        // Gọi Google API để xác minh Access Token
        String url = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" + accessToken;
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> userInfo = restTemplate.getForObject(url, Map.class);

        if (userInfo == null || !userInfo.containsKey("email")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("Login", false, "message", "Invalid Access Token"));
        }

        // Lấy thông tin email từ phản hồi Google
        String email = (String) userInfo.get("email");
        Random random = new Random();
        Role defaultRole = new Role();
        defaultRole.setRole_id(2L); // Đặt id vai trò mặc định là 2
        // Kiểm tra người dùng đã tồn tại hay chưa
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(email);
            newUser.setName("user "+random.nextInt());
            newUser.setRole(defaultRole);
            newUser.setPrivate(false);
            userRepository.save(newUser);
            return newUser;
        });

        Map<String, Object> userData = new HashMap<>(); // Tạo bản đồ cho dữ liệu người dùng
        userData.put("id", user.getUser_id());
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());
        userData.put("name", user.getName());
        userData.put("role", user.getRole().getRole_id());
        userData.put("avatar_url", user.getAvatar_url());

        // Tạo token cho người dùng
        String tokenValue = jwtUtil.generateToken(user.getUsername());
        Token token = new Token(tokenValue, LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(jwtUtil.getExpiration()), user);
        tokenRepository.save(token);
        return ResponseEntity.ok(Map.of(
                "Login", true,
                "data", Map.of("user", userData),
                "email", user.getEmail(),
                "token", tokenValue
        ));
    } catch (Exception e) {
        System.err.println("Error during Google API call: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Login", false, "message", "Error verifying Access Token: " + e.getMessage()));
    }
}

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
