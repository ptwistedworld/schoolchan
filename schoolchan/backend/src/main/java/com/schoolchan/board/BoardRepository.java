package com.schoolchan.board;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, UUID> {
    Optional<Board> findBySlug(String slug);
}
