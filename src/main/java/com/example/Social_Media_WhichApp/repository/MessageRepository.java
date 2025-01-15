package com.example.Social_Media_WhichApp.repository;

import com.example.Social_Media_WhichApp.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Các phương thức tìm kiếm tin nhắn, ví dụ:
    List<Message> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    List<Message> findByRoomId(Long roomId);
}