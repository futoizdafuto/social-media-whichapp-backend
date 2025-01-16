package com.example.Social_Media_WhichApp.services;

import com.example.Social_Media_WhichApp.entity.Message;
import com.example.Social_Media_WhichApp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Message sendMessageUser(Long senderId, Long receiverId, String content) {
        // Tạo đối tượng Message
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setReplyToMessageId(null);
        message.setStatus("SENT"); // Mặc định trạng thái là SENT
        message.setTimestamp(LocalDateTime.now());

        // Lưu tin nhắn vào cơ sở dữ liệu
        return messageRepository.save(message);
    }

}
