package com.example.Social_Media_WhichApp.controller;

import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.repository.UserRepository;
import com.example.Social_Media_WhichApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUser();
    }

    // Phương thức để xử lý đăng nhập của người dùng
    @PostMapping("/login") // Xử lý yêu cầu POST đến api/users/login
    public Map<String, Object> loginUser(@RequestParam String username, @RequestParam String password) {
        return userService.loginUser(username, password);
    }

    // Phương thức để xử lý đăng ký của người dùng
    @PostMapping("/register") // Xử lý yêu cầu POST đến api/users/register
    public Map<String, Object> registerUser(@RequestParam String username,
                                            @RequestParam String password,
                                            @RequestParam String email,
                                            @RequestParam String name) {
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

}
