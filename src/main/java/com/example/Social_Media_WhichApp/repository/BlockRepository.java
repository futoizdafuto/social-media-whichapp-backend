package com.example.Social_Media_WhichApp.repository;

import com.example.Social_Media_WhichApp.entity.Block;
import com.example.Social_Media_WhichApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {

    // Truy vấn xem người dùng đã block người khác chưa
    Optional<Block> findByBlockerAndBlocked(User blocker, User blocked);

    // Truy vấn các người dùng mà một người block
    List<Block> findByBlocker(User blocker);

    // Truy vấn các người block một người
    List<Block> findByBlocked(User blocked);
}

