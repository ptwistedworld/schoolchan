package com.schoolchan.staff;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationSightingRepository extends JpaRepository<ModerationSighting, UUID> {

    List<ModerationSighting> findByEscalationIdOrderByCreatedAtDesc(UUID escalationId);

    long countByEscalationId(UUID escalationId);
}
