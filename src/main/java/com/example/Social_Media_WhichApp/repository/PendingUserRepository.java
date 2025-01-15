package com.example.Social_Media_WhichApp.repository;

import com.example.Social_Media_WhichApp.entity.PendingUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PendingUserRepository  extends JpaRepository<PendingUser, Long> {
    Optional<PendingUser> findByEmail(String email);

    Optional<PendingUser> findByOtp(String otp);
    Optional<PendingUser> findByUsername(String username);
    void deleteByUsername(String username);
    // Xóa các PendingUser có thời gian hết hạn trước thời điểm chỉ định
    // Tìm tất cả các PendingUser có OTP đã hết hạn và isActivated = true
    List<PendingUser> findAllByOtpExpirationTimeBeforeAndIsActivatedTrue(LocalDateTime time);

    // Xóa tất cả các PendingUser có isActivated = false và OTPExpirationTime trước thời gian cho trước
    void deleteByIsActivatedFalseAndOtpExpirationTimeBefore(LocalDateTime time);
}
