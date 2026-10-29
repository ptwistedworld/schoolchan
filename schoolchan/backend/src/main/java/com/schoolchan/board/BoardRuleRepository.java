package com.schoolchan.board;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRuleRepository extends JpaRepository<BoardRule, UUID> {

    List<BoardRule> findByBoardIdAndEnabledTrue(UUID boardId);

    List<BoardRule> findByBoardIdOrderByPatternAsc(UUID boardId);
}
