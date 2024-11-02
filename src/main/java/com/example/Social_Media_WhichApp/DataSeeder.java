package com.example.Social_Media_WhichApp;

import com.example.Social_Media_WhichApp.entity.Role;
import com.example.Social_Media_WhichApp. entity.User;
import com.example.Social_Media_WhichApp.entity.Post;
import com.example.Social_Media_WhichApp.repository.RoleRepository;
import com.example.Social_Media_WhichApp.repository.UserRepository;
import com.example.Social_Media_WhichApp.repository.PostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initData(UserRepository userRepository, PostRepository postRepository, RoleRepository roleRepository) {
        return args -> {
//            Role admin = new Role();
//            admin.setType("admin");
//            roleRepository.save(admin);
//            Role user = new Role();
//            user.setType("user");
//            roleRepository.save(user);
////             Tạo người dùng mẫu
//            User user1 = new User();
//            user1.setName("a@example.com");
//            user1.setUsername("abc");
//            user1.setPassword("password123");
//            user1.setAvatar_url("http://192.168.0.189:8080/uploads/2.png");
//            user1.setRole(admin);
//            User user2 = new User();
//           user2.setName("Nguyen Van A");
//            user2.setUsername("abcd");
//            user2.setEmail("b@example.com");
//            user2.setPassword("password456");
//            user2.setAvatar_url("https://www.behance.net/gallery/163707203/Genshin-Impact-HeaderBanner-Raiden");
//            user2.setRole(user);
//            // Lưu người dùng vào cơ sở dữ liệu
//            userRepository.save(user1);
//            userRepository.save(user2);

//            // Tạo bài viết mẫu
//            Post post1 = new Post();
//            post1.setUser(user1);
//            post1.setContent("Hello World! Đây là bài viết đầu tiên.");
//            post1.setImg_url("http://192.168.0.189:8080/uploads/1.png");
//            post1.setCreated_at(new Date());
//
//            Post post2 = new Post();
//            post2.setUser(user2);
//            post2.setContent("Đây là bài viết thứ hai của tôi.");
//            post2.setVideo_url("http://192.168.0.189:8080/uploads/1.png");
//            post2.setCreated_at(new Date());
//
//            // Lưu bài viết vào cơ sở dữ liệu
//            postRepository.save(post1);
//            postRepository.save(post2);
        };
    }
}
