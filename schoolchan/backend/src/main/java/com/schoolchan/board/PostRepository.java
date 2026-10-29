package com.schoolchan.board;

<<<<<<< HEAD
=======
import java.util.Collection;
>>>>>>> d5851d3 (Initial commit)
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
<<<<<<< HEAD
=======
import org.springframework.data.repository.query.Param;
>>>>>>> d5851d3 (Initial commit)

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findByThreadIdOrderByCreatedAtAsc(UUID threadId);

    Optional<Post> findByBoardIdAndBoardPostNumber(UUID boardId, long boardPostNumber);

    @Query("select coalesce(max(p.boardPostNumber), 0) from Post p where p.boardId = :boardId")
    long maxBoardPostNumber(UUID boardId);
<<<<<<< HEAD
=======

    List<Post> findByOriginalTextContainingIgnoreCase(String query);

    List<Post> findByBoardId(UUID boardId);

    /** A set of posts by id, in one query. */
    @Query("select p from Post p where p.id in :ids")
    List<Post> findByIdIn(@Param("ids") Collection<UUID> ids);

    /** The posts that are waiting for a person to look at them. */
    List<Post> findByFlaggedTrue();

    List<Post> findByStatus(com.schoolchan.board.PostStatus status);

    /** The most recent posts, for the "every screened post" log. */
    List<Post> findTop200ByOrderByCreatedAtDesc();

    /**
     * The first published post of each thread, in one query.
     *
     * <p>The board page used to load every post of every thread just to show
     * the first one, which was sixty queries for one page of twenty threads.
     */
    @Query("""
            select p from Post p
            where p.status = com.schoolchan.board.PostStatus.PUBLISHED
              and p.threadId in :threadIds
              and p.boardPostNumber = (
                  select min(p2.boardPostNumber) from Post p2
                  where p2.threadId = p.threadId
                    and p2.status = com.schoolchan.board.PostStatus.PUBLISHED)
            """)
    List<Post> findFirstPublishedPerThread(@Param("threadIds") Collection<UUID> threadIds);
>>>>>>> d5851d3 (Initial commit)
}
