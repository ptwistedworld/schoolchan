package com.schoolchan.media;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaPhashBlocklistRepository extends JpaRepository<MediaPhashBlocklistEntry, UUID> {
    boolean existsByPhash(String phash);
}
