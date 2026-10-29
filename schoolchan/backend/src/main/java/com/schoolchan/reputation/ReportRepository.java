package com.schoolchan.reputation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    Optional<Report> findByPostIdAndSessionHash(UUID postId, String sessionHash);

    List<Report> findByPostId(UUID postId);
<<<<<<< HEAD
=======

    long countByReason(String reason);

    List<Report> findByStatusOrderByCreatedAtDesc(String status);

    List<Report> findByStatus(String status);
>>>>>>> d5851d3 (Initial commit)
}
