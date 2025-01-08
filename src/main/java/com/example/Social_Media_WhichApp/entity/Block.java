package com.example.Social_Media_WhichApp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "block")
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // id tự tăng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)  // blocked_id tham chiếu tới bảng User
    private User blocked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)  // blocker_id tham chiếu tới bảng User
    private User blocker;

    public Block() {
    }

    public Block(User blocked, User blocker) {
        this.blocked = blocked;
        this.blocker = blocker;
    }

    // Getter và Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getBlocked() {
        return blocked;
    }

    public void setBlocked(User blocked) {
        this.blocked = blocked;
    }

    public User getBlocker() {
        return blocker;
    }

    public void setBlocker(User blocker) {
        this.blocker = blocker;
    }
}
