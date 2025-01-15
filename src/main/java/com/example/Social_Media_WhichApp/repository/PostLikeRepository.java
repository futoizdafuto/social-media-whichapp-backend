package com.example.Social_Media_WhichApp.repository;

import com.example.Social_Media_WhichApp.entity.Post;
import com.example.Social_Media_WhichApp.entity.PostLike;
import com.example.Social_Media_WhichApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // Phương thức để tìm lượt "like" của một bài viết bởi một người dùng cụ thể
    Optional<PostLike> findByPostAndUser(Post post, User user);
}
