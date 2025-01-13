package com.example.Social_Media_WhichApp.entity;

import jakarta.persistence.*;
import java.util.Date;

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

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "is_waiting", nullable = false)
    private boolean isWaiting ;

    // Constructor mặc định
    public Follow() {
        // Khởi tạo createdAt với thời gian hiện tại khi tạo đối tượng Follow
        this.createdAt = new Date();
        // isWaiting mặc định là false
        this.isWaiting = false;
    }

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public void setWaiting(boolean isWaiting) {
        this.isWaiting = isWaiting;
    }

    // Hàm để cập nhật trạng thái isWaiting
    public void setWaitingToYes() {
        this.isWaiting = true;
    }
}
