package com.example.Social_Media_WhichApp.entity;


import jakarta.persistence.*;
import jakarta.persistence.Column;


@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user_name;
    private String email;
    private String password;

    @Column(length = 500)
    private String avatar_url;

    private String last_name;
    private String first_name;



    public User() {

    }

    public User(Long id, String user_name, String email, String password, String avatar_url, String last_name, String first_name) {
        this.id = id;
        this.user_name = user_name;
        this.email = email;
        this.password = password;
        this.avatar_url = avatar_url;
        this.last_name = last_name;
        this.first_name = first_name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public Long getId() {
        return id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
}
