package com.example.Social_Media_WhichApp.repository;

import com.example.Social_Media_WhichApp.entity.Post;
import com.example.Social_Media_WhichApp.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    // Phương thức để tìm tất cả các comment của một bài viết cụ thể
    List<PostComment> findByPost(Post post);
}
