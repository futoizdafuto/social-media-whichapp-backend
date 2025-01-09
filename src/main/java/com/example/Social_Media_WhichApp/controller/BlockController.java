package com.example.Social_Media_WhichApp.controller;

import com.example.Social_Media_WhichApp.services.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/users")
public class BlockController {

    @Autowired
    private BlockService blockService;

    // API Block (single user)
    @PostMapping("/block")
    public Map<String, Object> blockUser(@RequestParam String username,
                                         @RequestParam String targetUsername,
                                         @RequestParam String token) {
        return blockService.blockUser(username, targetUsername, token);
    }

    // API Unblock (single user)
    @PostMapping("/unblock")
    public Map<String, Object> unblockUser(@RequestParam String username,
                                           @RequestParam String targetUsername,
                                           @RequestParam String token) {
        return blockService.unblockUser(username, targetUsername, token);
    }

    // API Get all blocked users
    @GetMapping("/blocks")
    public Map<String, Object> getBlockedUsers(@RequestParam String username) {
        return blockService.getBlocks(username);
    }
    @GetMapping("/blockers")
    public Map<String, Object> getBlockerAndBlockedUsers() {
        return blockService.getBlockerAndBlockedUsers();
    }
}
