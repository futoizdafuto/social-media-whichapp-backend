package com.example.Social_Media_WhichApp.controller;

import com.example.Social_Media_WhichApp.entity.Notification;
import com.example.Social_Media_WhichApp.entity.PostComment;
import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.repository.NotificationRepository;
import com.example.Social_Media_WhichApp.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/notifications")
public class NotificationController {

//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    // Gửi thông báo đến user hiện tại qua WebSocket
//    public void sendNotification(String username, String message) {
//        messagingTemplate.convertAndSendToUser(
//                username,  // Tên user nhận thông báo
//                "/queue/notifications",  // Đích đến dành riêng
//                message   // Nội dung thông báo
//        );
//    }

}

