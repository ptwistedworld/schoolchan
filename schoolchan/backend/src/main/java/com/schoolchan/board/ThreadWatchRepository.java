package com.schoolchan.board;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadWatchRepository extends JpaRepository<ThreadWatch, UUID> {

    Optional<ThreadWatch> findByThreadIdAndSessionHash(UUID threadId, String sessionHash);

    List<ThreadWatch> findBySessionHashOrderByCreatedAtDesc(String sessionHash);

    long countByThreadIdAndSessionHashAndLastSeenPostLessThan(UUID threadId, String sessionHash, long post);
}
