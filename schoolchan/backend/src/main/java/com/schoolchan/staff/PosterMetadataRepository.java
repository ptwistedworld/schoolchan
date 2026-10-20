package com.schoolchan.staff;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PosterMetadataRepository extends JpaRepository<PosterMetadata, UUID> {

    Optional<PosterMetadata> findByPostId(UUID postId);

    @Modifying
    @Query("DELETE FROM PosterMetadata p WHERE p.createdAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") Instant cutoff);
}
