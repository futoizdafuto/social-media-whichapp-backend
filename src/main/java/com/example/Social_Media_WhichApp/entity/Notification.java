package com.example.Social_Media_WhichApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "notifications")
@JsonIgnoreProperties({"user", "post"}) // Tránh tuần tự hóa các trường không cần thiết
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người nhận thông báo

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = true)
    private Post post; // Bài viết liên quan, nếu có

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Boolean isread = false; // Đã đọc hay chưa

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public Notification(Long id, User user, Post post, String message, Boolean isread, Date createdAt) {
        this.id = id;
        this.user = user;
        this.post = post;
        this.message = message;
        this.isread = isread;
        this.createdAt = createdAt;
    }

    public Notification() {
    }

    // Getters và Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsread() {
        return isread;
    }

    public void setIsread(Boolean isread) {
        this.isread = isread;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


}