package com.schoolchan.media;

<<<<<<< HEAD
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<MediaEntity, UUID> {
    Optional<MediaEntity> findByStoredName(String storedName);

    List<MediaEntity> findByPostId(UUID postId);
=======
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MediaRepository extends JpaRepository<MediaEntity, UUID> {

    Optional<MediaEntity> findByStoredName(String storedName);

    List<MediaEntity> findByPostId(UUID postId);

    /**
     * Every released image attached to a post in any of these threads, oldest
     * post first. One query gives the board page a preview image for each
     * thread, including threads whose picture was in a reply.
     */
    @Query("""
            select m from MediaEntity m
            join com.schoolchan.board.Post p on m.postId = p.id
            where p.threadId in :threadIds
              and p.status = com.schoolchan.board.PostStatus.PUBLISHED
              and m.quarantineStatus = 'RELEASED'
              and m.contentType like 'image/%'
            order by p.threadId, p.boardPostNumber
            """)
    List<MediaEntity> findPreviewImagesForThreads(@Param("threadIds") Collection<UUID> threadIds);

    /** All the attachments of a set of posts, in one query. */
    @Query("select m from MediaEntity m where m.postId in :postIds")
    List<MediaEntity> findByPostIdIn(@Param("postIds") Collection<UUID> postIds);
>>>>>>> d5851d3 (Initial commit)
}
