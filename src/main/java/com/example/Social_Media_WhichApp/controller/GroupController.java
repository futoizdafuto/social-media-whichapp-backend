package com.example.Social_Media_WhichApp.controller;


import com.example.Social_Media_WhichApp.entity.Group;
import com.example.Social_Media_WhichApp.entity.GroupMember;
import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.exception.ForbiddenException;
import com.example.Social_Media_WhichApp.exception.ResourceNotFoundException;
import com.example.Social_Media_WhichApp.repository.GroupMemberRepository;
import com.example.Social_Media_WhichApp.repository.GroupRepository;
import com.example.Social_Media_WhichApp.repository.UserRepository;
import com.example.Social_Media_WhichApp.services.GroupService;
import com.example.Social_Media_WhichApp.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@CrossOrigin(origins = "http://localhost:8443")
@RestController
@RequestMapping("api/groups")
public class GroupController {
   @Autowired
    private MessageService messageService;

    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;

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
    @PostMapping("/create")
    public ResponseEntity<String> createGroup(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String avatar,
            @RequestParam Long adminUserId,
            @RequestParam List<Long> userIds
    ) {
        // Create the group
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setAvatar(avatar);
        group.setCreatedAt(LocalDateTime.now());
        group = groupRepository.save(group);

        // Create admin member
        GroupMember adminMember = new GroupMember(group, adminUserId, "admin", LocalDateTime.now());
        groupMemberRepository.save(adminMember);

        // Create other members
        List<GroupMember> members = new ArrayList<>();
        for (Long userId : userIds) {
            GroupMember member = new GroupMember(group, userId, "member", LocalDateTime.now());
            members.add(member);
        }
        groupMemberRepository.saveAll(members);

        return ResponseEntity.ok("Group created successfully with ID: " + group.getId());
    }



}
