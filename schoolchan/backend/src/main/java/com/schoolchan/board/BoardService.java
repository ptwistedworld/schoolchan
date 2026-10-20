package com.schoolchan.board;

import com.schoolchan.media.MediaService;
import com.schoolchan.moderation.ModerationPipeline;
import com.schoolchan.reputation.ReputationService;
import com.schoolchan.staff.MetadataCryptoService;
import com.schoolchan.staff.PosterMetadata;
import com.schoolchan.staff.PosterMetadataRepository;
import com.schoolchan.translation.TranslationClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final ThreadRepository threadRepository;
    private final PostRepository postRepository;
    private final VoteRepository voteRepository;
    private final ModerationPipeline moderationPipeline;
    private final TranslationClient translationClient;
    private final ReputationService reputationService;
    private final PosterMetadataRepository posterMetadataRepository;
    private final MetadataCryptoService metadataCryptoService;
    private final PostRateLimiter rateLimiter;
    private final MediaService mediaService;

    public BoardService(
            BoardRepository boardRepository,
            ThreadRepository threadRepository,
            PostRepository postRepository,
            VoteRepository voteRepository,
            ModerationPipeline moderationPipeline,
            TranslationClient translationClient,
            ReputationService reputationService,
            PosterMetadataRepository posterMetadataRepository,
            MetadataCryptoService metadataCryptoService,
            PostRateLimiter rateLimiter,
            MediaService mediaService) {
        this.boardRepository = boardRepository;
        this.threadRepository = threadRepository;
        this.postRepository = postRepository;
        this.voteRepository = voteRepository;
        this.moderationPipeline = moderationPipeline;
        this.translationClient = translationClient;
        this.reputationService = reputationService;
        this.posterMetadataRepository = posterMetadataRepository;
        this.metadataCryptoService = metadataCryptoService;
        this.rateLimiter = rateLimiter;
        this.mediaService = mediaService;
    }

    @Transactional(readOnly = true)
    public Board requireBoard(String slug) {
        return boardRepository.findBySlug(slug).orElseThrow(() -> new IllegalArgumentException("board not found"));
    }

    @Transactional(readOnly = true)
    public List<ThreadEntity> listActiveThreads(UUID boardId) {
        List<ThreadEntity> threads = new ArrayList<>();
        threads.addAll(threadRepository.findByBoardIdAndStatusOrderByPinnedDescBumpedAtDesc(boardId, ThreadStatus.ACTIVE));
        threads.addAll(threadRepository.findByBoardIdAndStatusOrderByPinnedDescBumpedAtDesc(boardId, ThreadStatus.SINKING));
        threads.sort(Comparator.comparing(ThreadEntity::isPinned).reversed()
                .thenComparing(ThreadEntity::getBumpedAt, Comparator.reverseOrder()));
        return threads;
    }

    @Transactional(readOnly = true)
    public List<ThreadEntity> listArchivedThreads(UUID boardId) {
        return threadRepository.findByBoardIdAndStatusOrderByPinnedDescBumpedAtDesc(boardId, ThreadStatus.ARCHIVED);
    }

    @Transactional(readOnly = true)
    public List<Post> listPosts(UUID threadId) {
        return postRepository.findByThreadIdOrderByCreatedAtAsc(threadId).stream()
                .filter(p -> p.getStatus() == PostStatus.PUBLISHED)
                .toList();
    }

    @Transactional
    public Post createThread(
            String boardSlug,
            String subject,
            String text,
            boolean sage,
            boolean spoiler,
            String sessionHash,
            String ip) {
        return createThread(boardSlug, subject, text, sage, spoiler, sessionHash, ip, null);
    }

    @Transactional
    public Post createThread(
            String boardSlug,
            String subject,
            String text,
            boolean sage,
            boolean spoiler,
            String sessionHash,
            String ip,
            MultipartFile file) {
        if (!rateLimiter.allow(sessionHash)) {
            throw new IllegalStateException("rate limit exceeded");
        }
        Board board = requireBoard(boardSlug);
        ThreadEntity thread = new ThreadEntity();
        thread.setId(UUID.randomUUID());
        thread.setBoardId(board.getId());
        thread.setSubject(subject);
        thread.setStatus(ThreadStatus.ACTIVE);
        thread.setLocked(false);
        thread.setPinned(false);
        thread.setImageCount(0);
        threadRepository.save(thread);

        Post post = buildPost(board, thread.getId(), text, sage, spoiler);
        post = postRepository.save(post);
        storeMetadata(post.getId(), sessionHash, ip);
        attachMedia(board, thread, post, file);
        applyModeration(post, sessionHash);
        post = postRepository.save(post);
        releaseIfPublished(post);
        thread.setReplyCount(0);
        threadRepository.save(thread);
        enforceActiveCap(board);
        return post;
    }

    @Transactional
    public Post reply(
            String boardSlug,
            UUID threadId,
            String text,
            boolean sage,
            boolean spoiler,
            String sessionHash,
            String ip) {
        return reply(boardSlug, threadId, text, sage, spoiler, sessionHash, ip, null);
    }

    @Transactional
    public Post reply(
            String boardSlug,
            UUID threadId,
            String text,
            boolean sage,
            boolean spoiler,
            String sessionHash,
            String ip,
            MultipartFile file) {
        if (!rateLimiter.allow(sessionHash)) {
            throw new IllegalStateException("rate limit exceeded");
        }
        Board board = requireBoard(boardSlug);
        ThreadEntity thread = threadRepository.findById(threadId).orElseThrow();
        if (thread.isLocked() || thread.getStatus() == ThreadStatus.ARCHIVED) {
            throw new IllegalStateException("thread locked");
        }

        Post post = buildPost(board, threadId, text, sage, spoiler);
        post = postRepository.save(post);
        storeMetadata(post.getId(), sessionHash, ip);
        attachMedia(board, thread, post, file);
        applyModeration(post, sessionHash);
        post = postRepository.save(post);
        releaseIfPublished(post);

        thread.setReplyCount(thread.getReplyCount() + 1);
        if (!sage && post.getStatus() == PostStatus.PUBLISHED) {
            thread.setBumpedAt(Instant.now());
        }
        if (thread.getReplyCount() >= board.getBumpLimit()) {
            thread.setStatus(ThreadStatus.SINKING);
        }
        threadRepository.save(thread);
        return post;
    }

    @Transactional
    public void vote(UUID postId, String sessionHash, short value) {
        if (voteRepository.findByPostIdAndSessionHash(postId, sessionHash).isPresent()) {
            return;
        }
        Post post = postRepository.findById(postId).orElseThrow();
        Vote vote = new Vote();
        vote.setId(UUID.randomUUID());
        vote.setPostId(postId);
        vote.setSessionHash(sessionHash);
        vote.setValue(value);
        voteRepository.save(vote);
        post.setScore(post.getScore() + value);
        postRepository.save(post);
    }

    void enforceActiveCap(Board board) {
        long active = threadRepository.countActiveByBoard(board.getId());
        if (active <= board.getActiveThreadCap()) {
            return;
        }
        List<ThreadEntity> oldest = threadRepository.findByBoardIdAndStatusOrderByBumpedAtAsc(
                board.getId(), ThreadStatus.ACTIVE);
        int toArchive = (int) (active - board.getActiveThreadCap());
        for (int i = 0; i < toArchive && i < oldest.size(); i++) {
            ThreadEntity t = oldest.get(i);
            if (!t.isPinned()) {
                t.setStatus(ThreadStatus.ARCHIVED);
                threadRepository.save(t);
            }
        }
    }

    private Post buildPost(Board board, UUID threadId, String text, boolean sage, boolean spoiler) {
        SpoilerParser.Result parsed = SpoilerParser.parse(text);
        Post post = new Post();
        post.setId(UUID.randomUUID());
        post.setThreadId(threadId);
        post.setBoardId(board.getId());
        post.setBoardPostNumber(postRepository.maxBoardPostNumber(board.getId()) + 1);
        post.setOriginalText(parsed.text());
        post.setStatus(PostStatus.PENDING_SCREENING);
        post.setSage(sage);
        post.setSpoiler(spoiler || parsed.spoiler());
        post.setSpoilerLabel(parsed.label());
        post.setScore(0);
        translationClient.translate(parsed.text()).ifPresent(tr -> {
            post.setTranslatedText(tr.translatedText());
            post.setDetectedLanguage(tr.detectedLanguage());
            post.setTranslationModel(tr.modelId());
        });
        return post;
    }

    private void applyModeration(Post post, String sessionHash) {
        PostStatus status = moderationPipeline.screen(post, sessionHash);
        if (reputationService.shouldAutoHold(sessionHash)) {
            status = PostStatus.HELD_FOR_REVIEW;
        }
        post.setStatus(status);
    }

    private void storeMetadata(UUID postId, String sessionHash, String ip) {
        PosterMetadata meta = new PosterMetadata();
        meta.setId(UUID.randomUUID());
        meta.setPostId(postId);
        meta.setIpEncrypted(metadataCryptoService.encrypt(ip == null ? "unknown" : ip));
        meta.setSessionHashEncrypted(metadataCryptoService.encrypt(sessionHash));
        posterMetadataRepository.save(meta);
    }

    private void attachMedia(Board board, ThreadEntity thread, Post post, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }
        if (thread.getImageCount() >= board.getImageLimit()) {
            throw new IllegalStateException("image limit reached");
        }
        try {
            mediaService.store(post.getId(), file);
        } catch (IOException ex) {
            throw new IllegalArgumentException("upload rejected");
        }
        thread.setImageCount(thread.getImageCount() + 1);
        threadRepository.save(thread);
    }

    private void releaseIfPublished(Post post) {
        if (post.getStatus() != PostStatus.PUBLISHED) {
            return;
        }
        mediaService.findByPost(post.getId()).forEach(m -> mediaService.release(m.getId()));
    }
}
