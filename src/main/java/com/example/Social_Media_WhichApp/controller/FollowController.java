package com.example.Social_Media_WhichApp.controller;

import com.example.Social_Media_WhichApp.services.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("api/users")
public class FollowController {
    @Autowired
    private FollowService followService;

    // API Follow
    @PostMapping("/follow")
    public Map<String, Object> follow(@RequestParam String username,
                                      @RequestParam String targetUsername,
                                      @RequestParam String token) {
        return followService.followUser(username, targetUsername, token);
    }

    // API Unfollow
    @PostMapping("/unfollow")
    public Map<String, Object> unfollow(@RequestParam String username,
                                        @RequestParam String targetUsername,
                                        @RequestParam String token) {
        return followService.unfollowUser(username, targetUsername, token);
    }
}
