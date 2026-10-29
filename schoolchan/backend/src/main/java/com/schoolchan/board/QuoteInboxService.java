package com.schoolchan.board;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * The "Quoted you" inbox.
 *
 * <p>Quote numbers are scoped to a board, so a reply only counts when it is in
 * the same board as the post it names. The index is built from the posts the
 * board already has, which keeps this to one query per board the reader has
 * posted on - and most readers only post on a handful.
 *
 * <p>Only replies a reader can actually see are returned. A hidden reply must
 * not reveal that it mentioned somebody, just as it does not show backlinks in
 * the thread.
 */
@Service
public class QuoteInboxService {

    private final PostRepository postRepository;
    private final PostSessionRepository postSessionRepository;
    private final ThreadRepository threadRepository;
    private final BoardRepository boardRepository;

    public QuoteInboxService(PostRepository postRepository,
                             PostSessionRepository postSessionRepository,
                             ThreadRepository threadRepository,
                             BoardRepository boardRepository) {
        this.postRepository = postRepository;
        this.postSessionRepository = postSessionRepository;
        this.threadRepository = threadRepository;
        this.boardRepository = boardRepository;
    }

    /** One visible reply, the thread it lives in, and which of your posts it named. */
    public record QuoteHit(Post reply, ThreadEntity thread, Board board, List<Long> yourNumbers) {
    }

    private static final class HitBuilder {
        private final Post reply;
        private final Set<Long> numbers = new HashSet<>();

        private HitBuilder(Post reply) {
            this.reply = reply;
        }
    }

    @Transactional(readOnly = true)
    public List<QuoteHit> forSession(String sessionHash) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return List.of();
        }
        Set<UUID> mineIds = new HashSet<>();
        for (PostSession s : postSessionRepository.findBySessionHashOrderByCreatedAtDesc(sessionHash)) {
            mineIds.add(s.getPostId());
        }
        if (mineIds.isEmpty()) {
            return List.of();
        }
        List<Post> mine = postRepository.findByIdIn(mineIds);
        Map<UUID, Set<Long>> myNumbersByBoard = new HashMap<>();
        for (Post p : mine) {
            myNumbersByBoard.computeIfAbsent(p.getBoardId(), key -> new HashSet<>())
                    .add(p.getBoardPostNumber());
        }
        if (myNumbersByBoard.isEmpty()) {
            return List.of();
        }

        Map<UUID, HitBuilder> hits = new HashMap<>();
        for (Map.Entry<UUID, Set<Long>> entry : myNumbersByBoard.entrySet()) {
            List<Post> boardPosts = postRepository.findByBoardId(entry.getKey());
            if (boardPosts.isEmpty()) {
                continue;
            }
            Map<Long, Post> byNumber = new HashMap<>();
            for (Post p : boardPosts) {
                byNumber.put(p.getBoardPostNumber(), p);
            }
            QuoteIndex index = QuoteIndex.of(boardPosts);
            for (Long myNumber : entry.getValue()) {
                for (Long replyNumber : index.backlinks(myNumber)) {
                    Post reply = byNumber.get(replyNumber);
                    if (reply == null || mineIds.contains(reply.getId())) {
                        continue;
                    }
                    if (reply.getStatus() != PostStatus.PUBLISHED || reply.isDeletedByAuthor()) {
                        continue;
                    }
                    hits.computeIfAbsent(reply.getId(), key -> new HitBuilder(reply)).numbers.add(myNumber);
                }
            }
        }
        if (hits.isEmpty()) {
            return List.of();
        }

        Map<UUID, ThreadEntity> threads = new HashMap<>();
        threadRepository.findAllById(hits.values().stream()
                        .map(hit -> hit.reply.getThreadId()).toList())
                .forEach(thread -> threads.put(thread.getId(), thread));
        Map<UUID, Board> boards = new HashMap<>();
        boardRepository.findAllById(myNumbersByBoard.keySet())
                .forEach(board -> boards.put(board.getId(), board));

        List<QuoteHit> out = new ArrayList<>();
        for (HitBuilder hit : hits.values()) {
            ThreadEntity thread = threads.get(hit.reply.getThreadId());
            Board board = boards.get(hit.reply.getBoardId());
            if (thread == null || board == null) {
                continue;
            }
            List<Long> numbers = new ArrayList<>(hit.numbers);
            Collections.sort(numbers);
            out.add(new QuoteHit(hit.reply, thread, board, numbers));
        }
        out.sort(Comparator.comparing((QuoteHit hit) -> hit.reply().getCreatedAt()).reversed()
                .thenComparing(hit -> hit.reply().getBoardPostNumber(), Comparator.reverseOrder()));
        return out;
    }
}
