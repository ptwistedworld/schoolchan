package com.schoolchan.student;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentAccountRepository extends JpaRepository<StudentAccount, UUID> {

    Optional<StudentAccount> findByStudentIdIgnoreCase(String studentId);

    List<StudentAccount> findAllByOrderByStudentIdAsc();

    long countByEnabledTrue();
}
