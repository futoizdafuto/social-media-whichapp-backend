package com.example.Social_Media_WhichApp.services;

import com.example.Social_Media_WhichApp.entity.Follow;
import com.example.Social_Media_WhichApp.entity.User;
import com.example.Social_Media_WhichApp.repository.FollowRepository;
import com.example.Social_Media_WhichApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FollowService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserService userService; // Used to check login status

    // Kiểm tra xem người dùng có tồn tại không
    private boolean checkUserExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    // Kiểm tra xem người dùng có đăng nhập không (sử dụng token)
    private boolean isUserLoggedIn(String token) {
        return userService.checkUserLoginStatus(token).get("status").equals("success");
    }

    // Theo dõi một người dùng (single user follow)
    public Map<String, Object> followUser(String username, String targetUsername, String token) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra xem người dùng có đăng nhập không
        if (!isUserLoggedIn(token)) {
            response.put("status", "error");
            response.put("message", "You need to log in to follow other users.");
            return response;
        }

        // Kiểm tra xem người dùng có tồn tại không
        if (!checkUserExists(targetUsername)) {
            response.put("status", "error");
            response.put("message", "User " + targetUsername + " does not exist.");
            return response;
        }

        User follower = userRepository.findByUsername(username);
        User followed = userRepository.findByUsername(targetUsername);

        // Kiểm tra xem người dùng đã theo dõi chưa
        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowed(follower, followed);
        if (existingFollow.isPresent()) {
            response.put("status", "error");
            response.put("message", "You are already following " + targetUsername + ".");
            return response;
        }

        // Lấy trạng thái của người được theo dõi (private hay public)
        Map<String, Object> statusResponse = userService.getUserStatus(targetUsername);

        // Debug: Kiểm tra giá trị của statusResponse
        System.out.println("Status Response: " + statusResponse);

        if ("success".equals(statusResponse.get("status"))) {
            boolean isPrivate = (boolean) statusResponse.get("private");

            // Debug: Kiểm tra giá trị của isPrivate
            System.out.println("Is Private: " + isPrivate);

            // Thêm quan hệ follow mới vào cơ sở dữ liệu
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowed(followed);

            // Cập nhật giá trị của isWaiting tùy vào trạng thái private/public
            follow.setWaiting(isPrivate);  // Nếu private thì là true (1), nếu public thì là false (0)

            // Debug: Kiểm tra giá trị của follow.isWaiting trước khi lưu
            System.out.println("isWaiting: " + follow.isWaiting());

            followRepository.save(follow);

            response.put("status", "success");
            response.put("message", "You are now following " + targetUsername);
        } else {
            response.put("status", "error");
            response.put("message", "Unable to fetch status for user " + targetUsername);
        }

        return response;
    }



    // Theo dõi nhiều người dùng (multiple user follow)
    public Map<String, Object> followMultipleUsers(String username, List<String> targetUsernames, String token) {
        Map<String, Object> response = new HashMap<>();
        List<String> successMessages = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        // Kiểm tra xem người dùng có đăng nhập không
        if (!isUserLoggedIn(token)) {
            response.put("status", "error");
            response.put("message", "You need to log in to follow other users.");
            return response;
        }

        // Kiểm tra xem người dùng có tồn tại không
        if (!checkUserExists(username)) {
            response.put("status", "error");
            response.put("message", "User " + username + " does not exist.");
            return response;
        }

        User follower = userRepository.findByUsername(username);

        // Lặp qua từng targetUsername và thực hiện hành động theo dõi
        for (String targetUsername : targetUsernames) {
            if (!checkUserExists(targetUsername)) {
                // Người dùng không tồn tại
                errorMessages.add("User " + targetUsername + " does not exist.");
                continue;
            }

            User followed = userRepository.findByUsername(targetUsername);

            // Kiểm tra xem người dùng đã follow chưa
            Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowed(follower, followed);
            if (existingFollow.isPresent()) {
                // Người dùng đã follow rồi
                errorMessages.add("You are already following " + targetUsername + ".");
                continue;
            }

            // Thêm quan hệ follow mới vào cơ sở dữ liệu
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowed(followed);
            followRepository.save(follow);

            successMessages.add("You are now following " + targetUsername);
        }

        // Xử lý kết quả trả về
        if (successMessages.isEmpty() && errorMessages.isEmpty()) {
            response.put("status", "error");
            response.put("message", "No valid users to follow.");
        } else {
            response.put("status", "success");
            response.put("message", "Follow attempt completed.");
            if (!successMessages.isEmpty()) {
                response.put("success_messages", successMessages);
            }
            if (!errorMessages.isEmpty()) {
                response.put("error_messages", errorMessages);
            }
        }

        return response;
    }

    // Bỏ theo dõi một người dùng (single user unfollow)
    public Map<String, Object> unfollowUser(String username, String targetUsername, String token) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra xem người dùng có đăng nhập không
        if (!isUserLoggedIn(token)) {
            response.put("status", "error");
            response.put("message", "You need to log in to unfollow users.");
            return response;
        }

        // Kiểm tra xem người dùng có tồn tại không
        if (!checkUserExists(targetUsername)) {
            response.put("status", "error");
            response.put("message", "User " + targetUsername + " does not exist.");
            return response;
        }

        User follower = userRepository.findByUsername(username);
        User followed = userRepository.findByUsername(targetUsername);

        // Kiểm tra xem người dùng có đang theo dõi người này không
        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowed(follower, followed);
        if (existingFollow.isEmpty()) {
            response.put("status", "error");
            response.put("message", "You are not following " + targetUsername + ".");
            return response;
        }

        // Xóa quan hệ follow khỏi cơ sở dữ liệu
        followRepository.delete(existingFollow.get());

        response.put("status", "success");
        response.put("message", "You have unfollowed " + targetUsername);
        return response;
    }

    // Bỏ theo dõi nhiều người dùng (multiple user unfollow)
    public Map<String, Object> unfollowMultipleUsers(String username, List<String> targetUsernames, String token) {
        Map<String, Object> response = new HashMap<>();
        List<String> successMessages = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        // Kiểm tra xem người dùng có đăng nhập không
        if (!isUserLoggedIn(token)) {
            response.put("status", "error");
            response.put("message", "You need to log in to unfollow users.");
            return response;
        }

        // Kiểm tra xem người dùng có tồn tại không
        if (!checkUserExists(username)) {
            response.put("status", "error");
            response.put("message", "User " + username + " does not exist.");
            return response;
        }

        User follower = userRepository.findByUsername(username);

        // Lặp qua từng targetUsername và thực hiện hành động unfollow
        for (String targetUsername : targetUsernames) {
            if (!checkUserExists(targetUsername)) {
                // Người dùng không tồn tại
                errorMessages.add("User " + targetUsername + " does not exist.");
                continue;
            }

            User followed = userRepository.findByUsername(targetUsername);

            // Kiểm tra xem người dùng có đang theo dõi người này không
            Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowed(follower, followed);
            if (existingFollow.isEmpty()) {
                // Người dùng không theo dõi rồi
                errorMessages.add("You are not following " + targetUsername + ".");
                continue;
            }

            // Xóa quan hệ follow khỏi cơ sở dữ liệu
            followRepository.delete(existingFollow.get());

            successMessages.add("You have unfollowed " + targetUsername);
        }

        // Xử lý kết quả trả về
        if (successMessages.isEmpty() && errorMessages.isEmpty()) {
            response.put("status", "error");
            response.put("message", "No valid users to unfollow.");
        } else {
            response.put("status", "success");
            response.put("message", "Unfollow attempt completed.");
            if (!successMessages.isEmpty()) {
                response.put("success_messages", successMessages);
            }
            if (!errorMessages.isEmpty()) {
                response.put("error_messages", errorMessages);
            }
        }

        return response;
    }

    // Lấy danh sách những người mà người dùng đang follow và số lượng người đã được follow
    public Map<String, Object> getFollows(String username) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra xem người dùng có tồn tại không
        if (!checkUserExists(username)) {
            response.put("status", "error");
            response.put("message", "User " + username + " does not exist.");
            return response;
        }

        // Lấy thông tin người dùng từ username
        User user = userRepository.findByUsername(username);

        // Lấy trạng thái private của người dùng
        Map<String, Object> userStatus = userService.getUserStatus(username);
        boolean isPrivate = (boolean) userStatus.get("private");

        // Lấy tất cả người dùng mà user này đang follow
        List<Follow> follows = followRepository.findByFollower(user);

        // Lọc các tài khoản mà is_waiting = 0, tức là những người mà không trong trạng thái chờ
        List<String> followedUsernames = follows.stream()
                .filter(follow -> !follow.isWaiting()) // Chỉ lấy những người không trong trạng thái waiting
                .map(follow -> follow.getFollowed().getUsername())
                .collect(Collectors.toList());

        // Lấy tất cả người đang theo dõi user này (followed list)
        List<Follow> followers = followRepository.findByFollowed(user);

        // Lọc danh sách những người theo dõi user, loại bỏ các mục có is_waiting = 1
        List<String> followerUsernames = followers.stream()
                .filter(follow -> !follow.isWaiting()) // Loại bỏ người theo dõi có trạng thái waiting
                .map(follow -> follow.getFollower().getUsername())
                .collect(Collectors.toList());

        // Nếu user đang ở trạng thái private, cần loại bỏ thêm các mục không phù hợp
        if (isPrivate) {
            follows.stream()
                    .filter(Follow::isWaiting) // Lấy danh sách các mục đang ở trạng thái waiting
                    .map(follow -> follow.getFollowed().getUsername())
                    .forEach(followedUsernames::remove); // Loại bỏ khỏi danh sách following
        }

        // Tính số lượng người đang follow và được follow
        int followCount = followedUsernames.size();
        int followedCount = followerUsernames.size();

        // Trả về kết quả
        response.put("status", "success");
        response.put("message", "Fetched following and followed users successfully.");
        response.put("following_list", followedUsernames); // Danh sách người mà user đang theo dõi
        response.put("followed_list", followerUsernames); // Danh sách những người theo dõi user
        response.put("following_count", followCount); // Số lượng người mà user đang theo dõi
        response.put("followed_count", followedCount); // Số lượng người đang theo dõi user

        return response;
    }
    public List<String> getWaitingUsers(String username) {
        // Kiểm tra xem người dùng có tồn tại không
        if (!checkUserExists(username)) {
            throw new IllegalArgumentException("User " + username + " does not exist.");
        }

        // Lấy danh sách tài khoản đang chờ duyệt
        return followRepository.findWaitingUsersByFollowedUsername(username);
    }
    public Map<String, Object> updateFollowStatus(String username, String targetUsername, String token) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra xem người dùng có đăng nhập không
        if (!isUserLoggedIn(token)) {
            response.put("status", "error");
            response.put("message", "You need to log in to update follow status.");
            return response;
        }

        // Kiểm tra xem người dùng có tồn tại không
        if (!checkUserExists(username) || !checkUserExists(targetUsername)) {
            response.put("status", "error");
            response.put("message", "User " + (checkUserExists(username) ? targetUsername : username) + " does not exist.");
            return response;
        }

        // Lấy thông tin người dùng
        User followed = userRepository.findByUsername(username);  // Người được theo dõi
        User follower = userRepository.findByUsername(targetUsername);  // Người theo dõi

        // Tìm quan hệ Follow giữa user và targetUser
        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowed(follower, followed);

        // Kiểm tra nếu không có mối quan hệ follow nào giữa 2 người này
        if (existingFollow.isEmpty()) {
            response.put("status", "error");
            response.put("message", "There is no follow relationship between " + username + " and " + targetUsername + ".");
            return response;
        }

        Follow follow = existingFollow.get();

        // Kiểm tra nếu trạng thái is_waiting = 1 thì thay đổi thành 0, nếu là 0 thì không thay đổi
        if (follow.isWaiting()) {
            follow.setWaiting(false);  // Đặt trạng thái is_waiting = 0
            followRepository.save(follow);
            response.put("status", "success");
            response.put("message", "The follow status has been updated to 'approved' (is_waiting = 0).");
        } else {
            response.put("status", "success");
            response.put("message", "The follow status is already 'approved' (is_waiting = 0). No changes made.");
        }

        return response;
    }





    public List<String> getAllUsernames() {
        return userRepository.findAllUsernames();  // Lấy danh sách username
    }

}
