package com.example.Social_Media_WhichApp.controller;

import com.example.Social_Media_WhichApp.entity.Message;
import com.example.Social_Media_WhichApp.security.JwtUtil;
import com.example.Social_Media_WhichApp.exception.ForbiddenException;
import com.example.Social_Media_WhichApp.exception.ResourceNotFoundException;
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
// thu ồi tin nhắn
    @DeleteMapping("/messages/{messageId}/delete")
    public ResponseEntity<String> deleteMessage(@PathVariable Long messageId,
                                                @RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        try {
            String result = messageService.deleteMessage(messageId, userId);
            return ResponseEntity.ok(result); // Xóa nhóm thành công
        } catch (
                ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        } catch (
                ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body( e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }

}
