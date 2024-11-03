package com.example.Social_Media_WhichApp.repository;

import com.example.Social_Media_WhichApp.entity.Token;
import com.example.Social_Media_WhichApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRepository   extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    void deleteByUserAndDeviceId(User user, String deviceId);

    //    void deleteByUser(User user);
    void deleteByExpiresAtBefore(LocalDateTime now);// Phương thức xóa token đã hết hạn

}
