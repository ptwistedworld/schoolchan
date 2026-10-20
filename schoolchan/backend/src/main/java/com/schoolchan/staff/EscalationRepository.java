package com.schoolchan.staff;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EscalationRepository extends JpaRepository<Escalation, UUID> {
    List<Escalation> findByStatusOrderByCreatedAtDesc(String status);

    List<Escalation> findAllByOrderByCreatedAtDesc();
}
