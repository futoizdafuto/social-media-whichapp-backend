package com.example.Social_Media_WhichApp.repository;

import com.example.Social_Media_WhichApp.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface PostRepository extends JpaRepository<Post, Long>{
 List<Post> findByUser_Username(String username);

}
