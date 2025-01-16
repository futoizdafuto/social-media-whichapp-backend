package com.example.Social_Media_WhichApp.controller;

import com.example.Social_Media_WhichApp.entity.Message;
import com.example.Social_Media_WhichApp.security.JwtUtil;
import com.example.Social_Media_WhichApp.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:8443")
@RestController
@RequestMapping("api/chat")
public class ChatController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping(value = "/getMessages/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> getMessagesUser(
            @RequestParam("senderId") Long senderId,
            @RequestParam("receiverId") Long receiverId) {

        // Lấy danh sách tin nhắn
        List<Message> messages = messageService.getMessagesUser(senderId, receiverId);

        // Tạo response trả về
        Map<String, Object> response = Map.of(
                "getMessagesUser", Map.of(
                        "status", "success",
                        "data", Map.of(
                                "messages", messages.stream().map(message -> Map.of(
                                        "messageId", message.getId(),
                                        "senderId", message.getSenderId(),
                                        "receiverId", message.getReceiverId(),
                                        "message", message.getContent(),
                                        "replyToMessageId", message.getReplyToMessageId(),
                                        "timestamp", message.getTimestamp()
                                )).collect(Collectors.toList())
                        )
                )
        );

        return ResponseEntity.ok(response);
    }


    // API lấy ra danh sách tin nhắn của nhóm
    @PostMapping(value = "/getMessages/group", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> getGroupMessages(
            @RequestParam("groupId") Long groupId) {

        // Lấy danh sách tin nhắn
        List<Message> messages = messageService.getGroupMessages(groupId);

        // Tạo response trả về theo yêu cầu
        Map<String, Object> response = Map.of(
                "getMessagesGroup", Map.of(
                        "status", "success",
                        "groupId", groupId,
                        "data", Map.of(
                                "messages", messages.stream().map(message -> Map.of(
                                        "messageId", message.getId(),
                                        "senderId", message.getSenderId(),
                                        "groupId", message.getRoomId(),
                                        "message", message.getContent(),
                                        "replyToMessageId", message.getReplyToMessageId(),
                                        "timestamp", message.getTimestamp()
                                )).collect(Collectors.toList())
                        )
                )
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @MessageMapping("/sendMessages/user")
    @SendTo("/topic/messages")
    public Map<String, Object> sendMessageUser(Message message, StompHeaderAccessor headerAccessor) {
        // Lấy token từ header
        String token = headerAccessor.getFirstNativeHeader("Authorization");
        if (token == null || !jwtUtil.validateToken(token.replace("Bearer ", ""))) {
            throw new IllegalArgumentException("Invalid or missing token");
        }

        // Lấy senderId từ token
        String senderUsername = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));

        // Kiểm tra nếu senderId trong request không khớp với token
        if (!message.getSenderId().equals(senderUsername)) {
            throw new IllegalArgumentException("Sender ID mismatch");
        }

        // Xử lý lưu trữ tin nhắn
        Message savedMessage = messageService.sendMessageUser(
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent()
        );

        // Tạo dữ liệu tin nhắn gửi đi
        Map<String, Object> response = new HashMap<>();
        response.put("messageId", savedMessage.getId());
        response.put("senderId", savedMessage.getSenderId());
        response.put("receiverId", savedMessage.getReceiverId());
        response.put("content", savedMessage.getContent());
        response.put("timestamp", savedMessage.getTimestamp());

        return response; // Gửi tin nhắn đến topic
    }



}
