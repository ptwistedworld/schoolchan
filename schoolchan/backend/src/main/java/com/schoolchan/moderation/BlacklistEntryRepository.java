package com.schoolchan.moderation;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistEntryRepository extends JpaRepository<BlacklistEntry, UUID> {
    List<BlacklistEntry> findByEnabledTrue();
}
