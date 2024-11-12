package com.example.Social_Media_WhichApp.entity;
import jakarta.persistence.*;
@Entity

public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id")  // Khóa ngoại cho người theo dõi
    private User follower;

    @ManyToOne
    @JoinColumn(name = "followed_id")  // Khóa ngoại cho người bị theo dõi
    private User followed;

//    @Column(name = "user_id")
//    private Long userId;  // Thuộc tính user_id thêm vào

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public User getFollowed() {
        return followed;
    }

    public void setFollowed(User followed) {
        this.followed = followed;
    }

//    public Long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Long userId) {
//        this.userId = userId;
//    }
}
