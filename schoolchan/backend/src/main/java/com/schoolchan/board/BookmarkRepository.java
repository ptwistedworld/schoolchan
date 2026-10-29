package com.schoolchan.board;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {

    Optional<Bookmark> findBySessionHashAndThreadId(String sessionHash, UUID threadId);

    List<Bookmark> findBySessionHashOrderByCreatedAtDesc(String sessionHash);

    long countBySessionHash(String sessionHash);

    /** Which of these threads this browser has bookmarked, in one query. */
    @Query("select b.threadId from Bookmark b where b.sessionHash = :sessionHash and b.threadId in :threadIds")
    List<UUID> findBookmarkedThreadIds(@Param("sessionHash") String sessionHash,
                                      @Param("threadIds") Collection<UUID> threadIds);
}
