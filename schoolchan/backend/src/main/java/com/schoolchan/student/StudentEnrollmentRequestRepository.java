package com.schoolchan.student;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentEnrollmentRequestRepository extends JpaRepository<StudentEnrollmentRequest, UUID> {

    List<StudentEnrollmentRequest> findByStatusOrderByCreatedAtDesc(String status);

    List<StudentEnrollmentRequest> findByRequestedByOrderByCreatedAtDesc(String requestedBy);
}
