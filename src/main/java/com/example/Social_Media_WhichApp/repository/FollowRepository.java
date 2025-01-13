package com.example.Social_Media_WhichApp.repository;

import com.example.Social_Media_WhichApp.entity.Follow;
import com.example.Social_Media_WhichApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository  extends JpaRepository<Follow, Long> {

    // Truy vấn xem người dùng đã theo dõi người dùng khác chưa
    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);

    // Truy vấn các người dùng mà một người theo dõi
    List<Follow> findByFollower(User follower);

    // Truy vấn các người theo dõi một người dùng
    List<Follow> findByFollowed(User followed);
    @Query("SELECT f.follower.username FROM Follow f WHERE f.followed.username = :username AND f.isWaiting = true")
    List<String> findWaitingUsersByFollowedUsername(@Param("username") String username);

}
