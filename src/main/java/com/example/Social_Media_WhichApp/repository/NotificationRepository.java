package com.example.Social_Media_WhichApp.repository;

import com.example.Social_Media_WhichApp.entity.Notification;
import com.example.Social_Media_WhichApp.entity.Post;
import com.example.Social_Media_WhichApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Lấy danh sách thông báo cho một người dùng
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}