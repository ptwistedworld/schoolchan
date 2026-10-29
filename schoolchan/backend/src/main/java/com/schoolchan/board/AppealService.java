package com.schoolchan.board;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * A student can ask for a held post to be looked at again. The appeal carries
 * only the anonymous session hash, so the staff member reviewing it cannot
 * tell who asked.
 */
@Service
public class AppealService {

    private final AppealRepository appeals;

    public AppealService(AppealRepository appeals) {
        this.appeals = appeals;
    }

    @Transactional
    public Appeal create(UUID postId, String sessionHash, String reason) {
        Appeal appeal = new Appeal();
        appeal.setId(UUID.randomUUID());
        appeal.setPostId(postId);
        appeal.setSessionHash(sessionHash == null ? "" : sessionHash);
        appeal.setReason(reason == null || reason.isBlank() ? null : reason.trim());
        appeal.setStatus("PENDING");
        return appeals.save(appeal);
    }

    @Transactional
    public void resolve(UUID appealId, boolean approved) {
        appeals.findById(appealId).ifPresent(a -> {
            a.setStatus(approved ? "APPROVED" : "DECLINED");
            a.setReviewedAt(Instant.now());
            appeals.save(a);
        });
    }

    @Transactional(readOnly = true)
    public List<Appeal> pending() {
        return appeals.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    @Transactional(readOnly = true)
    public List<Appeal> forPost(UUID postId) {
        return appeals.findByPostIdOrderByCreatedAtDesc(postId);
    }

    @Transactional(readOnly = true)
    public long pendingCount() {
        return appeals.countByStatus("PENDING");
    }
}
