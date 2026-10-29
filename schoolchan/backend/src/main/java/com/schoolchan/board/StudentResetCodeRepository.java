package com.schoolchan.board;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentResetCodeRepository extends JpaRepository<StudentResetCode, UUID> {

    Optional<StudentResetCode> findByStudentIdAndCodeAndUsedFalse(String studentId, String code);

    List<StudentResetCode> findByStudentIdOrderByCreatedAtDesc(String studentId);
}
