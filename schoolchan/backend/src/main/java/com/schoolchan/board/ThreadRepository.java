package com.schoolchan.board;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThreadRepository extends JpaRepository<ThreadEntity, UUID> {

    List<ThreadEntity> findByBoardIdAndStatusOrderByPinnedDescBumpedAtDesc(UUID boardId, ThreadStatus status);

<<<<<<< HEAD
    @Query("SELECT COUNT(t) FROM ThreadEntity t WHERE t.boardId = :boardId AND t.status = com.schoolchan.board.ThreadStatus.ACTIVE")
=======
    @Query("SELECT COUNT(t) FROM ThreadEntity t WHERE t.boardId = :boardId AND (t.status = com.schoolchan.board.ThreadStatus.ACTIVE OR t.status = com.schoolchan.board.ThreadStatus.SINKING)")
>>>>>>> d5851d3 (Initial commit)
    long countActiveByBoard(@Param("boardId") UUID boardId);

    List<ThreadEntity> findByBoardIdAndStatusOrderByBumpedAtAsc(UUID boardId, ThreadStatus status);
}
