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



}
