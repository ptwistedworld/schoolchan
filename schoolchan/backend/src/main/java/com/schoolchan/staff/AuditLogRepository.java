package com.schoolchan.staff;

import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<< HEAD
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {}
=======
import java.util.List;
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findAllByOrderByCreatedAtDesc();
}
>>>>>>> d5851d3 (Initial commit)
