package com.example.Social_Media_WhichApp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_members")
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group; // Liên kết với nhóm

    @Column(nullable = false)
    private Long userId; // ID của user tham gia nhóm

    @Column(nullable = false)
    private String role; // Vai trò trong nhóm (member với manager)

    @Column(nullable = false)
    private LocalDateTime joinedAt; // Ngày giờ tham gia nhóm

    // Constructors
    public GroupMember() {
    }

    public GroupMember(Group group, Long userId, String role, LocalDateTime joinedAt) {
        this.group = group;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
