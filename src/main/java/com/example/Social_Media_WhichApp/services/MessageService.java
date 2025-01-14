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


    // Gửi tin nhắn 1x1
    public Message sendMessageUser(Long receiverId, String content) {
        Message message = new Message();
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setStatus("SENT");
        message.setTimestamp(LocalDateTime.now());
        // Gửi tin nhắn với id người gửi được giả định là người đang đăng nhập
        message.setSenderId(1L);  // Thay bằng ID của người gửi thực tế
        return messageRepository.save(message);
    }

    // Gửi tin nhắn trong group
    public Message sendMessageGroup(Long groupId, String content) {
        Message message = new Message();
        message.setRoomId(groupId);
        message.setContent(content);
        message.setStatus("SENT");
        message.setTimestamp(LocalDateTime.now());
        // Gửi tin nhắn với id người gửi được giả định là người đang đăng nhập
        message.setSenderId(1L);  // Thay bằng ID của người gửi thực tế
        return messageRepository.save(message);
    }


    // Trả lời tin nhắn
    public Message replyMessage(Long replyToMessageId, String content) {
        Message message = new Message();
        message.setReplyToMessageId(replyToMessageId);
        message.setContent(content);
        message.setStatus("SENT");
        message.setTimestamp(LocalDateTime.now());
        // Gửi tin nhắn với id người gửi được giả định là người đang đăng nhập
        message.setSenderId(1L);  // Thay bằng ID của người gửi thực tế
        return messageRepository.save(message);
    }
}
