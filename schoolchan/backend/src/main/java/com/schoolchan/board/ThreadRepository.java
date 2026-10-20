package com.schoolchan.board;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThreadRepository extends JpaRepository<ThreadEntity, UUID> {

    List<ThreadEntity> findByBoardIdAndStatusOrderByPinnedDescBumpedAtDesc(UUID boardId, ThreadStatus status);

    @Query("SELECT COUNT(t) FROM ThreadEntity t WHERE t.boardId = :boardId AND t.status = com.schoolchan.board.ThreadStatus.ACTIVE")
    long countActiveByBoard(@Param("boardId") UUID boardId);

    List<ThreadEntity> findByBoardIdAndStatusOrderByBumpedAtAsc(UUID boardId, ThreadStatus status);
}
