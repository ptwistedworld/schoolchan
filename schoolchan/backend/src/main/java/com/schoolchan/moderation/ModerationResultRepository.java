package com.schoolchan.moderation;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationResultRepository extends JpaRepository<ModerationResult, UUID> {
    Optional<ModerationResult> findFirstByPostIdOrderByCreatedAtDesc(UUID postId);
}
