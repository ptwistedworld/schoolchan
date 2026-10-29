package com.schoolchan.staff;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffNoteRepository extends JpaRepository<StaffNote, UUID> {

    List<StaffNote> findByThreadIdOrderByCreatedAtDesc(UUID threadId);

    List<StaffNote> findByPostIdOrderByCreatedAtDesc(UUID postId);
}
