package com.schoolchan.board;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppealRepository extends JpaRepository<Appeal, UUID> {

    List<Appeal> findByStatusOrderByCreatedAtDesc(String status);

    List<Appeal> findByPostIdOrderByCreatedAtDesc(UUID postId);

    long countByStatus(String status);
}
