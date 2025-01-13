package com.example.Social_Media_WhichApp.services;

import com.example.Social_Media_WhichApp.entity.PendingUser;
import com.example.Social_Media_WhichApp.entity.Role;
import com.example.Social_Media_WhichApp.entity.Token;
import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.repository.PendingUserRepository;
import com.example.Social_Media_WhichApp.repository.TokenRepository;
import com.example.Social_Media_WhichApp.repository.UserRepository;
import com.example.Social_Media_WhichApp.security.EncryptPassword;
import com.example.Social_Media_WhichApp.security.JwtUtil;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    // ./auth/userinfo.email
    // ./auth/userinfo.profile
    // 87591578803-eag78ddgkvdiidtb84ji60v1a9o9vpl7.apps.googleusercontent.com
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MailService mailService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PendingUserRepository pendingUserRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    /**
     * Scheduled task to update isActivated status for expired OTPs
     */
    @Scheduled(fixedRate = 1000) // Chạy mỗi giây
    @Transactional
    public void updateExpiredPendingUsers() {
        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();

        // Cập nhật isActivated thành false nếu OTP đã hết hạn
        List<PendingUser> expiredUsers = pendingUserRepository.findAllByOtpExpirationTimeBeforeAndIsActivatedTrue(now);
        for (PendingUser user : expiredUsers) {
            user.setActivated(false);
            pendingUserRepository.save(user);
        }

        System.out.println("Updated expired pending users at: " + now);
    }

    /**
     * Scheduled task to delete pending users older than 1 hour with isActivated = false
     */
    @Scheduled(fixedRate = 3600000) // Chạy mỗi giờ
    @Transactional
    public void deleteInactivePendingUsers() {
        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);

        // Xóa các user trong PendingUser có isActivated = false và quá thời gian 1 tiếng
        pendingUserRepository.deleteByIsActivatedFalseAndOtpExpirationTimeBefore(oneHourAgo);

        System.out.println("Deleted inactive pending users at: " + now);
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

        // Kiểm tra email đã tồn tại
        if (userRepository.findByEmail(email).isPresent()) {
            response.put("register", Map.of(
                    "status", "error",
                    "message", "Email already exists"
            ));
            return response;
        }

//<<<<<<< HEAD
//=======
//        newUser.setEmail(email);
//        newUser.setName(name);
//        newUser.setRole(defaultRole); // Thiết lập vai trò cho người dùng
//        newUser.setPrivate(false); // Thiết lập cho người dùng trạng thái là public
//>>>>>>> 249ccebb112faae3b43caf10aeeee0454a8b1328

        // Tạo mã OTP
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        LocalDateTime otpExpiry = LocalDateTime.now().plusMinutes(2);

        // Tìm bản ghi email đã tồn tại trong PendingUser
        Optional<PendingUser> existingPendingUser = pendingUserRepository.findByEmail(email);

        // Lưu vào PendingUser hoặc ghi đè nếu tồn tại
        PendingUser pendingUser = existingPendingUser.orElse(new PendingUser());
        pendingUser.setUsername(username);
        try {
            pendingUser.setPassword(EncryptPassword.encrypt(password)); // Mã hóa mật khẩu
        } catch (Exception e) {
            response.put("register", Map.of(
            "status", "error",
            "message", "Encryption error"
            ));
            return response;
        }
        pendingUser.setEmail(email);
        pendingUser.setName(name);
        pendingUser.setOtp(otp);
        pendingUser.setOtpExpirationTime(otpExpiry);
        pendingUser.setActivated(true);

        pendingUserRepository.save(pendingUser);

        mailService.sendOtp(email, otp);
        //        // Cập nhật phản hồi
        response.put("register", Map.of(
                "status", "success",
                "email", email,
                "message", "Please authenticate your gmail to complete the registration procedure."
        ));

        return response; // Trả về phản hồi
    }


    public Map<String, Object> verifyOtp(String email, String otp) {
        Map<String, Object> response = new HashMap<>();

        // Tìm PendingUser bằng email
        Optional<PendingUser> optionalPendingUser = pendingUserRepository.findByEmail(email);

        if (optionalPendingUser.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Email does not exist or is invalid.");
            return response;
        }

        PendingUser pendingUser = optionalPendingUser.get();

        // Kiểm tra nếu tài khoản chưa kích hoạt
        if (!pendingUser.isActivated()) {
            response.put("status", "error");
            response.put("message", "OTP has expired. Please register again.");
            return response;
        }

        // Kiểm tra OTP
        if (!pendingUser.getOtp().equals(otp)) {
            response.put("status", "error");
            response.put("message", "OTP is not valid.");
            return response;
        }

        // Kiểm tra thời gian hết hạn OTP
        if (pendingUser.getOtpExpirationTime().isBefore(LocalDateTime.now())) {
            pendingUserRepository.delete(pendingUser); // Xóa bản ghi nếu OTP hết hạn
            response.put("status", "error");
            response.put("message", "OTP has expired. Please try again.");
            return response;
        }

        // Tạo vai trò mặc định
        Role defaultRole = new Role();
        defaultRole.setRole_id(2L); // Đặt id vai trò mặc định là 2


        // Chuyển từ PendingUser sang User
        User newUser = new User();
        newUser.setUsername(pendingUser.getUsername());
        newUser.setPassword(pendingUser.getPassword());
        newUser.setEmail(pendingUser.getEmail());
        newUser.setName(pendingUser.getName());
        newUser.setRole(defaultRole);
        newUser.setPrivate(false); // Thiết lập cho người dùng trạng thái là public

        // Lưu User và xóa PendingUser
        userRepository.save(newUser);
        pendingUserRepository.delete(pendingUser);

        // Phản hồi thành công
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", newUser.getUsername());
        userData.put("email", newUser.getEmail());
        userData.put("name", newUser.getName());
        userData.put("role", newUser.getRole().getRole_id());

        response.put("status", "success");
        response.put("message", "OTP successfully authenticated. User registration successful.");
        response.put("data", Map.of("user", userData));

        return response;
    }





    // Đăng nhập bằng username
    public Map<String, Object> loginUser(String username, String password) {
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
                            LocalDateTime.now().plusSeconds(jwtUtil.getExpiration()), user);
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
                            "password", "error",
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

    @Transactional // Đảm bảo tính nhất quán của giao dịch
    public Map<String, Object> logoutUser(String tokenValue) {
        // Tìm kiếm token trong cơ sở dữ liệu
        Optional<Token> token = tokenRepository.findByToken(tokenValue);

        Map<String, Object> response = new HashMap<>();

        if (token.isPresent()) {
            // Nếu token tồn tại, xóa token khỏi cơ sở dữ liệu
            tokenRepository.delete(token.get());
            response.put("status", "success");
            response.put("message", "User logged out successfully");
            response.put("login", false); // Thêm thuộc tính login
        } else {
            // Token không tồn tại hoặc không hợp lệ
            response.put("status", "error");
            response.put("message", "Invalid or expired token");
        }

        return Map.of("logout", response); // Bọc thông tin trong đối tượng logout
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
    public Map<String, Object> reLogin(String tokenValue) throws Exception {
        Map<String, Object> response = new HashMap<>();
        Optional<Token> optionalToken = tokenRepository.findByToken(tokenValue);

        if (optionalToken.isPresent() && optionalToken.get().getExpiresAt().isAfter(LocalDateTime.now())) {
            User user = optionalToken.get().getUser();
            tokenRepository.delete(optionalToken.get());

            String newTokenValue = jwtUtil.generateToken(user.getUsername());
            Token newToken = new Token(newTokenValue, LocalDateTime.now(),
                    LocalDateTime.now().plusSeconds(jwtUtil.getExpiration()), user);
            tokenRepository.save(newToken);

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getUser_id());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("name", user.getName());
            userData.put("role", user.getRole().getRole_id());
            userData.put("avatar_url", user.getAvatar_url());

            response.put("relogin", Map.of(
                    "Login", true,
                    "status", "success",
                    "message", "Login successful with existing token",
                    "newToken", newTokenValue,
                    "data", Map.of("user", userData),
                    "time", LocalDateTime.now()
            ));
        } else {
            response.put("login", Map.of(
                    "Login", false,
                    "status", "error",
                    "message", "Invalid or expired token"
            ));
        }
        return response;
    }
     public Map<String, Object> updatePrivate(String username) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findByUsername(username);

        if (user != null) {
            user.setPrivate(true);  // Set the user's profile to private
            userRepository.save(user);  // Save the updated user
            response.put("status", "success");
            response.put("message", "User " + username + " profile updated to private");
        } else {
            response.put("status", "error");
            response.put("message", "User " + username + " not found");
        }
        return response;
    }

    // Update user privacy status to public (0)
    public Map<String, Object> updatePublic(String username) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findByUsername(username);

        if (user != null) {
            user.setPrivate(false);  // Set the user's profile to public
            userRepository.save(user);  // Save the updated user
            response.put("status", "success");
            response.put("message", "User " + username + " profile updated to public");
        } else {
            response.put("status", "error");
            response.put("message", "User " + username + " not found");
        }
        return response;
    }
    // Phương thức lấy trạng thái của người dùng
    public Map<String, Object> getUserStatus(String username) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findByUsername(username);

        if (user != null) {
            // Trả về trạng thái (private hoặc public)
            response.put("status", "success");
            response.put("message", "User status fetched successfully.");
            response.put("username", username);
            response.put("private", user.isPrivate());  // True nếu là private, false nếu public
        } else {
            response.put("status", "error");
            response.put("message", "User " + username + " not found");
        }
        return response;
    }






}
