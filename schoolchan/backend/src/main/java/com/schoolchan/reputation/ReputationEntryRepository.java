package com.schoolchan.reputation;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReputationEntryRepository extends JpaRepository<ReputationEntry, UUID> {
    Optional<ReputationEntry> findBySessionHash(String sessionHash);
}
