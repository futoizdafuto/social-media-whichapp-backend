package com.example.Social_Media_WhichApp.service;

import com.example.Social_Media_WhichApp.entity.Post;
import com.example.Social_Media_WhichApp.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PostService {

    private PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post createPost (Post post) {
        return postRepository.save(post);
    }

}
