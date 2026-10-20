package com.schoolchan.board;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findByThreadIdOrderByCreatedAtAsc(UUID threadId);

    Optional<Post> findByBoardIdAndBoardPostNumber(UUID boardId, long boardPostNumber);

    @Query("select coalesce(max(p.boardPostNumber), 0) from Post p where p.boardId = :boardId")
    long maxBoardPostNumber(UUID boardId);
}
