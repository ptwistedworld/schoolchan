package com.schoolchan.moderation;

import com.schoolchan.board.Post;

public interface ModerationStage {
    String name();

    StageResult evaluate(Post post, String combinedText);
}
