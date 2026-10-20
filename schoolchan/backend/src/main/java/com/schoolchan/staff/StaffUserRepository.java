package com.schoolchan.staff;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffUserRepository extends JpaRepository<StaffUser, UUID> {
    Optional<StaffUser> findByUsername(String username);
}
