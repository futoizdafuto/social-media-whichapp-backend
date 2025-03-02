package com.example.Social_Media_WhichApp.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 500)
    private String avatar_url;

    private String username;

//    @JsonIgnore // bỏ qua không hiển thị trong json api
    private String password;
    private String email;
    private String name;

    // 1 user có thể có nhiều bài viết
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Post> posts;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    @JsonManagedReference //để quản lý quan hệ của Role
    private Role role;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate = false;  // Default value set to false (public)

    @Column(length = 10, nullable = true)
    private String gender;  // Có thể null, ví dụ: "male", "female", hoặc "other"

    @Column(nullable = true)
    private LocalDate birthday;  // Có thể null, lưu ngày sinh

    @Column(name = "is_banned", nullable = false)
    private boolean isBanned = false;  // Default value set to false (not banned)


    public User() {
    }

    public User(Long id) {
        this.userId = id;
    }

    public User(Long id, String avatar_url, String username, String password, String email, String name, List<Post> posts, Role role, boolean isPrivate, String gender, LocalDate birthday, boolean isBanned) {
        this.userId = id;
        this.avatar_url = avatar_url;
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.posts = posts;
        this.role = role;
        this.isPrivate = isPrivate;
        this.gender = gender;
        this.birthday = birthday;
        this.isBanned = isBanned;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    // Getter and Setter for isPrivate
    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long id) {
        this.userId = id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
