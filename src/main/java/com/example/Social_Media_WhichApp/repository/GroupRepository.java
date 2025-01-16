package com.example.Social_Media_WhichApp.repository;

import com.example.Social_Media_WhichApp.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
  @Query("SELECT g FROM Group g JOIN g.members m WHERE m.userId = :userId AND m.role = :role")
    List<Group> findGroupsByUserIdAndRole(@Param("userId") Long userId, @Param("role") String role);
}
