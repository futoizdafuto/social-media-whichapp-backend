package com.example.Social_Media_WhichApp.controller;


import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.exception.ForbiddenException;
import com.example.Social_Media_WhichApp.exception.ResourceNotFoundException;
import com.example.Social_Media_WhichApp.services.GroupService;
import com.example.Social_Media_WhichApp.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "http://localhost:8443")
@RestController
@RequestMapping("api/groups")
public class GroupController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private GroupService groupService;

    @GetMapping("/{groupId}/users")
    public ResponseEntity<List<User>> getUsersByGroupId(@PathVariable Long groupId) {
        List<User> users = groupService.getUsersByGroupId(groupId);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{groupId}/delete")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId,
                                         @RequestParam Long userId) {
        try {
            String result = groupService.deleteGroup(groupId, userId);
            return ResponseEntity.ok(result); // Xóa nhóm thành công
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body( e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }



}
