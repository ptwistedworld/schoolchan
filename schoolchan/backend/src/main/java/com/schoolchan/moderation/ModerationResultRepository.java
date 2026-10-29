package com.schoolchan.moderation;

<<<<<<< HEAD
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationResultRepository extends JpaRepository<ModerationResult, UUID> {
    Optional<ModerationResult> findFirstByPostIdOrderByCreatedAtDesc(UUID postId);
=======
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ModerationResultRepository extends JpaRepository<ModerationResult, UUID> {

    Optional<ModerationResult> findFirstByPostIdOrderByCreatedAtDesc(UUID postId);

    /**
     * Moderation results older than the cutoff. The table grows by one row
     * per post, so it is trimmed on a schedule to keep the database small.
     */
    @Modifying
    @Query("delete from ModerationResult r where r.createdAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") Instant cutoff);

    long countByCreatedAtAfter(Instant cutoff);
>>>>>>> d5851d3 (Initial commit)
}
