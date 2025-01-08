package com.example.Social_Media_WhichApp.repository;

import com.example.Social_Media_WhichApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
//    User findByEmail(String email);
    User findByUsername(String username);

    Optional<User> findByEmail(String email);

}
