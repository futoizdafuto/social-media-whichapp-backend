package com.example.Social_Media_WhichApp.services;

import com.example.Social_Media_WhichApp.entity.Message;
import com.example.Social_Media_WhichApp.exception.ForbiddenException;
import com.example.Social_Media_WhichApp.exception.ResourceNotFoundException;
import com.example.Social_Media_WhichApp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    // Lấy danh sách tin nhắn 1x1
    public List<Message> getMessagesUser(Long senderId, Long receiverId) {
        return messageRepository.findBySenderIdAndReceiverId(senderId, receiverId);
    }


    // Lấy danh sách tin nhắn của group
    public List<Message> getGroupMessages(Long groupId) {
        return messageRepository.findByRoomId(groupId);
    }


    public String deleteMessage(Long messageId, Long userId) {
        // Tìm tin nhắn theo ID
        Message message = messageRepository.findById(messageId).orElse(null);

        // Kiểm tra nếu không tìm thấy tin nhắn
        if (message == null) {
            throw new ResourceNotFoundException("Tin nhắn không tồn tại.");
        }


        // Kiểm tra xem người yêu cầu có phải là người gửi tin nhắn không
        if (!message.getSenderId().equals(userId)) {
            throw new ForbiddenException("Chỉ người gửi tin nhắn mới có thể thu hồi tin nhắn.");
        }

        // Kiểm tra xem tin nhắn đã được gửi quá 30 phút chưa
        LocalDateTime sentTime = message.getTimestamp();
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(sentTime, currentTime);

        if (duration.toMinutes() > 30) {
            throw new ForbiddenException("Tin nhắn chỉ có thể thu hồi trong vòng 30 phút.");
        }

        // Nếu tất cả điều kiện đúng, xóa tin nhắn
        messageRepository.deleteById(messageId);
        return "Tin nhắn đã được thu hồi thành công.";
    }
}
