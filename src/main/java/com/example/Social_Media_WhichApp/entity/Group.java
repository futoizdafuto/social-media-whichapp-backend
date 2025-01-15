package com.example.Social_Media_WhichApp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "group_messages")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Tên nhóm

    @Column
    private String description; // Mô tả nhóm (có thể null)

    @Column
    private String avatar; // Đường dẫn ảnh đại diện nhóm (có thể null)

    @Column(nullable = false)
    private LocalDateTime createdAt; // Ngày giờ tạo nhóm

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> members; // Danh sách thành viên trong nhóm

    // Constructors
    public Group() {
    }

    public Group(String name, String description, String avatar, LocalDateTime createdAt) {
        this.name = name;
        this.description = description;
        this.avatar = avatar;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<GroupMember> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMember> members) {
        this.members = members;
    }
}
