package com.schoolchan.moderation;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnedBlacklistCandidateRepository extends JpaRepository<LearnedBlacklistCandidate, UUID> {
    List<LearnedBlacklistCandidate> findByApprovedFalseOrderByCreatedAtDesc();

    boolean existsByPatternAndApprovedFalse(String pattern);
}
