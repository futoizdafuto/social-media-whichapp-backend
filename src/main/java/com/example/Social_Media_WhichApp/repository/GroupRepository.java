package com.example.Social_Media_WhichApp.repository;

import com.example.Social_Media_WhichApp.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}
