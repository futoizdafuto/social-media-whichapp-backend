package com.example.Social_Media_WhichApp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId; // ID người gửi

    @Column
    private Long receiverId; // ID người nhận (1x1 chat)

    @Column
    private Long roomId; // ID phòng (tin nhắn nhóm)

    @Column(nullable = false)
    private String content; // Nội dung tin nhắn

    @Column
    private Long replyToMessageId; // ID tin nhắn trả lời (nếu có)

    @Column(nullable = false)
    private String status; // Trạng thái tin nhắn (e.g., SENT, READ, DELETED)

    @Column(nullable = false)
    private LocalDateTime timestamp; // Thời gian gửi tin nhắn

    // Constructors
    public Message() {
    }

    public Message(Long senderId, Long receiverId, Long roomId, String content, Long replyToMessageId, String status, LocalDateTime timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.roomId = roomId;
        this.content = content;
        this.replyToMessageId = replyToMessageId;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(Long replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
