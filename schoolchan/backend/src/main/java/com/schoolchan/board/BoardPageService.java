package com.schoolchan.board;

import com.schoolchan.media.MediaEntity;
import com.schoolchan.media.MediaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Builds the data a board page needs, in a fixed number of queries.
 *
 * <p>The board page used to ask for each thread separately: every post of
 * every thread, then every attachment of the first post, then whether this
 * browser had bookmarked it. That was sixty queries for one page of twenty
 * threads, and it got worse the busier the board got.
 */
@Service
public class BoardPageService {

    private final PostRepository postRepository;
    private final MediaRepository mediaRepository;
    private final BookmarkRepository bookmarkRepository;

    public BoardPageService(PostRepository postRepository,
                            MediaRepository mediaRepository,
                            BookmarkRepository bookmarkRepository) {
        this.postRepository = postRepository;
        this.mediaRepository = mediaRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    /** Everything the thread list, the catalog and the scroll loader need. */
    public record PageData(Map<UUID, Post> firstPost,
                            Map<UUID, MediaEntity> previewImage,
                            Map<UUID, Boolean> bookmarked) {
    }

    @Transactional(readOnly = true)
    public PageData load(Collection<UUID> threadIds, String sessionHash) {
        Map<UUID, Post> firstPost = new LinkedHashMap<>();
        Map<UUID, MediaEntity> previewImage = new LinkedHashMap<>();
        Map<UUID, Boolean> bookmarked = new HashMap<>();

        if (threadIds == null || threadIds.isEmpty()) {
            return new PageData(firstPost, previewImage, bookmarked);
        }

        // 1. the opening post of each thread
        for (Post p : postRepository.findFirstPublishedPerThread(threadIds)) {
            firstPost.put(p.getThreadId(), p);
        }

        // 2. a picture for each thread. The one on the opening post, or the
        //    earliest one in a reply, so a thread whose picture was in a reply
        //    still shows something on the board.
        List<MediaEntity> images = mediaRepository.findPreviewImagesForThreads(threadIds);
        if (!images.isEmpty()) {
            Set<UUID> mediaPostIds = new HashSet<>();
            for (MediaEntity m : images) {
                mediaPostIds.add(m.getPostId());
            }
            Map<UUID, UUID> threadOfPost = new HashMap<>();
            for (Post p : postRepository.findByIdIn(mediaPostIds)) {
                threadOfPost.put(p.getId(), p.getThreadId());
            }
            for (MediaEntity m : images) {
                UUID threadId = threadOfPost.get(m.getPostId());
                if (threadId != null) {
                    previewImage.putIfAbsent(threadId, m);
                }
            }
        }

        // 3. which of these threads this browser has bookmarked
        Set<UUID> saved = new HashSet<>();
        if (sessionHash != null && !sessionHash.isBlank()) {
            saved.addAll(bookmarkRepository.findBookmarkedThreadIds(sessionHash, threadIds));
        }
        for (UUID id : threadIds) {
            bookmarked.put(id, saved.contains(id));
        }

        return new PageData(firstPost, previewImage, bookmarked);
    }

    /** Every attachment of a set of threads, keyed by thread, in one query. */
    @Transactional(readOnly = true)
    public Map<UUID, List<MediaEntity>> mediaByThread(Collection<UUID> threadIds) {
        Map<UUID, List<MediaEntity>> out = new HashMap<>();
        if (threadIds == null || threadIds.isEmpty()) {
            return out;
        }
        for (MediaEntity m : mediaRepository.findPreviewImagesForThreads(threadIds)) {
            Post p = postRepository.findById(m.getPostId()).orElse(null);
            if (p == null) {
                continue;
            }
            out.computeIfAbsent(p.getThreadId(), k -> new ArrayList<>()).add(m);
        }
        return out;
    }
}
