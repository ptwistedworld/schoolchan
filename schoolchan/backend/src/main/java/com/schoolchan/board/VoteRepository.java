package com.schoolchan.board;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, UUID> {
    Optional<Vote> findByPostIdAndSessionHash(UUID postId, String sessionHash);
    long countByPostId(UUID postId);
}
