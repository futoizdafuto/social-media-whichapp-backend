package com.example.Social_Media_WhichApp.controller;

import com.example.Social_Media_WhichApp.services.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/users")
public class FollowController {

    @Autowired
    private FollowService followService;

    // API Follow (single user)
    @PostMapping("/follow")
    public Map<String, Object> follow(@RequestParam String username,
                                      @RequestParam String targetUsername,
                                      @RequestParam String token) {
        return followService.followUser(username, targetUsername, token);
    }

    // API Follow (multiple users)
    @PostMapping("/follows")
    public Map<String, Object> followMultiple(@RequestParam String username,
                                              @RequestParam List<String> targetUsernames,
                                              @RequestParam String token) {
        return followService.followMultipleUsers(username, targetUsernames, token);
    }

    // API Unfollow (single user)
    @PostMapping("/unfollow")
    public Map<String, Object> unfollow(@RequestParam String username,
                                        @RequestParam String targetUsername,
                                        @RequestParam String token) {
        return followService.unfollowUser(username, targetUsername, token);
    }

    // API Unfollow (multiple users)
    @PostMapping("/unfollows")
    public Map<String, Object> unfollowMultiple(@RequestParam String username,
                                                @RequestParam List<String> targetUsernames,
                                                @RequestParam String token) {
        return followService.unfollowMultipleUsers(username, targetUsernames, token);
    }

    // API Get all follows of a user along with the count
    @GetMapping("/follows")
    public Map<String, Object> getFollows(@RequestParam String username) {
        return followService.getFollows(username);
    }
    @GetMapping("/usernames")
    public List<String> getAllUsernames() {
        return followService.getAllUsernames();
    }
    @GetMapping("/waitingusers")
    public Map<String, Object> getWaitingUsers(@RequestParam String username) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<String> waitingUsers = followService.getWaitingUsers(username);
            response.put("status", "success");
            response.put("waiting_users", waitingUsers);
        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;
    }
    @PostMapping("/updatestatus")
    public Map<String, Object> updateFollowStatus(@RequestParam String username,
                                                  @RequestParam String targetUsername,
                                                  @RequestParam String token) {
        return followService.updateFollowStatus(username, targetUsername, token);
    }




}
