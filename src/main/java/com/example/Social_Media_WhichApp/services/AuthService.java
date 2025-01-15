package com.example.Social_Media_WhichApp.services;

import com.example.Social_Media_WhichApp.entity.Auth;
import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    public Auth generateOtp(User user, String purpose) {
        String otp = generateRandomOtp(); // Hàm sinh mã OTP ngẫu nhiên
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(2); // OTP hết hạn sau 2 phút

        Auth auth = new Auth(otp, expiryTime, purpose, user);
        authRepository.save(auth);

        return auth;
    }

    private String generateRandomOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // Sinh mã OTP 6 chữ số
    }


    public void updateOtp(Auth auth) {
        auth.setOtp(generateRandomOtp()); // Tạo OTP mới (hàm generateNewOtp tự định nghĩa)
        auth.setExpiryTime(LocalDateTime.now().plusMinutes(10)); // Cập nhật thời gian hết hạn
        auth.setVerified(false); // Đặt lại trạng thái chưa xác minh
        authRepository.save(auth); // Lưu thay đổi
    }

}
