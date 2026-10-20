package com.schoolchan.access;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessWindowRepository extends JpaRepository<AccessWindow, UUID> {
    List<AccessWindow> findByEnabledTrue();
}
