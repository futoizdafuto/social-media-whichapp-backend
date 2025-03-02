package com.example.Social_Media_WhichApp.services;

import com.example.Social_Media_WhichApp.entity.Group;
import com.example.Social_Media_WhichApp.entity.GroupMember;
import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.exception.ForbiddenException;
import com.example.Social_Media_WhichApp.exception.ResourceNotFoundException;
import com.example.Social_Media_WhichApp.repository.GroupMemberRepository;
import com.example.Social_Media_WhichApp.repository.GroupRepository;
import com.example.Social_Media_WhichApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserRepository userRepository;

    public List<User> getUsersByGroupId(Long groupId) {
        // Lấy danh sách GroupMember theo groupId
        List<GroupMember> groupMembers = groupMemberRepository.findByGroupId(groupId);

        // Lấy danh sách userId từ GroupMember
        List<Long> userIds = groupMembers.stream()
                .map(GroupMember::getUserId)
                .collect(Collectors.toList());

        // Truy vấn thông tin User từ danh sách userId
        return userRepository.findByUserIdIn(userIds);
    }

    public String deleteGroup(Long groupId, Long userId) {
        // Kiểm tra sự tồn tại của nhóm
        Optional<Group> groupOptional = groupRepository.findById(groupId);
        if (!groupOptional.isPresent()) {
            throw new ResourceNotFoundException("Nhóm không tồn tại.");
        }

        Group group = groupOptional.get();

        // Kiểm tra nếu người dùng có phải là thành viên của nhóm hay không
        Optional<GroupMember> groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (!groupMember.isPresent()) {
            throw new ForbiddenException("Bạn không phải là thành viên của nhóm này.");
        }

        GroupMember member = groupMember.get();

        // Kiểm tra quyền của người dùng, nếu không phải quản trị viên (role = 2)
        if (member.getRole().equals("member")) {
            throw new ForbiddenException("Chỉ quản trị viên mới có thể xóa nhóm.");
        }

        // Xóa nhóm
        groupRepository.delete(group);
        return "Bạn đã xóa thành công nhóm: " + group.getName();
    }

    public Group createGroup(String name, String description, String avatar, Long adminUserId, List<Long> userIds) {
        // Nếu avatar không được cung cấp, sử dụng ảnh mặc định
        if (avatar == null || avatar.isEmpty()) {
            avatar = "https://example.com/default-avatar.png";
        }

        // Tạo đối tượng Group với thời gian hiện tại
        Group group = new Group(name, description, avatar, LocalDateTime.now());

        // Lưu nhóm mới vào database
        Group savedGroup = groupRepository.save(group);

        // Tạo thành viên với vai trò "admin"
        GroupMember adminMember = new GroupMember(savedGroup, adminUserId, "admin", LocalDateTime.now());
        groupMemberRepository.save(adminMember);

        // Lặp qua danh sách userIds và thêm từng thành viên với vai trò "member"
        userIds.forEach(userId -> {
            GroupMember member = new GroupMember(savedGroup, userId, "member", LocalDateTime.now());
            groupMemberRepository.save(member);
        });

        // Trả về nhóm đã tạo
        return savedGroup;
    }
}
