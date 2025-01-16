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
                                         @RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
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
    @PostMapping("/users/groups")
    public ResponseEntity<Map<String, Object>> getUserGroupsByPost(@RequestParam Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "User ID is required"
            ));
        }

        List<Group> groups = groupMemberRepository.findByUserId(userId).stream()
                .map(GroupMember::getGroup)
                .collect(Collectors.toList());

        // Format dữ liệu trả về
        List<Map<String, Object>> groupData = groups.stream().map(group -> {
            Map<String, Object> groupInfo = new HashMap<>();
            groupInfo.put("roomId", group.getId());
            groupInfo.put("name", group.getName());
            groupInfo.put("description", group.getDescription());
            groupInfo.put("avatar", group.getAvatar());
            groupInfo.put("createdAt", group.getCreatedAt());
            return groupInfo;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("getUserGroups", Map.of(
                "data", Map.of("groups", groupData),
                "status", "success"
        ));
        return ResponseEntity.ok(response);
    }


    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> joinGroup(@RequestParam Long groupId,
                                                         @RequestParam Long userId) {
        if (groupId == null || userId == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Group ID and User ID are required"
            ));
        }

        // Kiểm tra nếu group tồn tại
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + groupId));

        // Kiểm tra nếu user đã tồn tại trong group
        boolean isAlreadyMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId).isPresent();
        if (isAlreadyMember) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "error",
                    "message", "User is already a member of this group"
            ));
        }

        // Thêm user vào group với vai trò member
        GroupMember groupMember = new GroupMember(group, userId, "member", LocalDateTime.now());
        groupMemberRepository.save(groupMember);

        // Chuẩn bị dữ liệu trả về
        Map<String, Object> joinData = new HashMap<>();
        joinData.put("userId", userId);
        joinData.put("roomId", groupId);
        joinData.put("role", "member");
        joinData.put("joinedAt", groupMember.getJoinedAt());

        Map<String, Object> response = new HashMap<>();
        response.put("joinGroup", Map.of(
                "data", joinData,
                "status", "success"
        ));

        return ResponseEntity.ok(response);
    }


}
