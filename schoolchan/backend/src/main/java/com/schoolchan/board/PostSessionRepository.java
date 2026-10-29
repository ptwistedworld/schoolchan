package com.schoolchan.board;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostSessionRepository extends JpaRepository<PostSession, UUID> {

    List<PostSession> findBySessionHashOrderByCreatedAtDesc(String sessionHash);

    void deleteByPostId(UUID postId);
}
