package com.schoolchan.staff;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RulesVersionRepository extends JpaRepository<RulesVersion, UUID> {
    Optional<RulesVersion> findTopByOrderByVersionDesc();
}
