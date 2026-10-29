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
<<<<<<< HEAD
=======
import java.time.temporal.ChronoUnit;
>>>>>>> d5851d3 (Initial commit)
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
<<<<<<< HEAD
=======
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
>>>>>>> d5851d3 (Initial commit)

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
<<<<<<< HEAD
=======
    private final PostSessionRepository postSessionRepository;
    private final DuplicateDetector duplicateDetector;
    private final ExecutorService translationExecutor = Executors.newFixedThreadPool(2);
    private int editWindowMinutes = 10;
    /** How long the author may still delete their own post. */
    public static final int DELETE_WINDOW_MINUTES = 15;
>>>>>>> d5851d3 (Initial commit)

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
<<<<<<< HEAD
            MediaService mediaService) {
=======
            MediaService mediaService,
            PostSessionRepository postSessionRepository,
            DuplicateDetector duplicateDetector) {
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
=======
        this.postSessionRepository = postSessionRepository;
        this.duplicateDetector = duplicateDetector;
>>>>>>> d5851d3 (Initial commit)
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

<<<<<<< HEAD
=======
    /**
     * Every post in a thread, including held and blocked ones. Callers must not
     * render the body of a non-published post — the thread view shows a
     * tombstone instead so post numbers stay continuous and quote links resolve.
     */
    @Transactional(readOnly = true)
    public List<Post> listPostsIncludingHidden(UUID threadId) {
        return postRepository.findByThreadIdOrderByCreatedAtAsc(threadId);
    }

>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
        if (!rateLimiter.allow(sessionHash)) {
=======
        return createThread(boardSlug, subject, text, sage, spoiler, sessionHash, ip, file, null);
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
            MultipartFile file,
            String tag) {
        if (!rateLimiter.allow(sessionHash, ip)) {
>>>>>>> d5851d3 (Initial commit)
            throw new IllegalStateException("rate limit exceeded");
        }
        Board board = requireBoard(boardSlug);
        ThreadEntity thread = new ThreadEntity();
        thread.setId(UUID.randomUUID());
        thread.setBoardId(board.getId());
<<<<<<< HEAD
        thread.setSubject(subject);
=======
        thread.setSubject(autoTitle(subject, text));
        thread.setTag(tag);
>>>>>>> d5851d3 (Initial commit)
        thread.setStatus(ThreadStatus.ACTIVE);
        thread.setLocked(false);
        thread.setPinned(false);
        thread.setImageCount(0);
        threadRepository.save(thread);

        Post post = buildPost(board, thread.getId(), text, sage, spoiler);
        post = postRepository.save(post);
        storeMetadata(post.getId(), sessionHash, ip);
<<<<<<< HEAD
=======
        recordPostSession(post.getId(), sessionHash);
>>>>>>> d5851d3 (Initial commit)
        attachMedia(board, thread, post, file);
        applyModeration(post, sessionHash);
        post = postRepository.save(post);
        releaseIfPublished(post);
<<<<<<< HEAD
=======
        scheduleTranslation(post);
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
        if (!rateLimiter.allow(sessionHash)) {
=======
        if (!rateLimiter.allow(sessionHash, ip)) {
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
=======
        recordPostSession(post.getId(), sessionHash);
>>>>>>> d5851d3 (Initial commit)
        attachMedia(board, thread, post, file);
        applyModeration(post, sessionHash);
        post = postRepository.save(post);
        releaseIfPublished(post);
<<<<<<< HEAD
=======
        scheduleTranslation(post);
>>>>>>> d5851d3 (Initial commit)

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
<<<<<<< HEAD
    public void vote(UUID postId, String sessionHash, short value) {
        if (voteRepository.findByPostIdAndSessionHash(postId, sessionHash).isPresent()) {
            return;
=======
    public boolean vote(UUID postId, String sessionHash, short value) {
        if (voteRepository.findByPostIdAndSessionHash(postId, sessionHash).isPresent()) {
            return false;
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
=======
        return true;
>>>>>>> d5851d3 (Initial commit)
    }

    void enforceActiveCap(Board board) {
        long active = threadRepository.countActiveByBoard(board.getId());
        if (active <= board.getActiveThreadCap()) {
            return;
        }
<<<<<<< HEAD
        List<ThreadEntity> oldest = threadRepository.findByBoardIdAndStatusOrderByBumpedAtAsc(
                board.getId(), ThreadStatus.ACTIVE);
=======
        List<ThreadEntity> oldest = new ArrayList<>();
        oldest.addAll(threadRepository.findByBoardIdAndStatusOrderByBumpedAtAsc(board.getId(), ThreadStatus.ACTIVE));
        oldest.addAll(threadRepository.findByBoardIdAndStatusOrderByBumpedAtAsc(board.getId(), ThreadStatus.SINKING));
        oldest.sort(Comparator.comparing(ThreadEntity::getBumpedAt));
>>>>>>> d5851d3 (Initial commit)
        int toArchive = (int) (active - board.getActiveThreadCap());
        for (int i = 0; i < toArchive && i < oldest.size(); i++) {
            ThreadEntity t = oldest.get(i);
            if (!t.isPinned()) {
                t.setStatus(ThreadStatus.ARCHIVED);
                threadRepository.save(t);
            }
        }
    }

<<<<<<< HEAD
=======
    /**
     * If the poster left the subject blank, make one from the first line of
     * the post so the board stays tidy.
     */
    static String autoTitle(String subject, String text) {
        if (subject != null && !subject.isBlank()) {
            String trimmed = subject.trim();
            return trimmed.length() > 120 ? trimmed.substring(0, 120) + "…" : trimmed;
        }
        String firstLine = "";
        if (text != null) {
            for (String line : text.split("\\R")) {
                String l = line.replaceFirst("^>+\\s*", "").trim();
                if (!l.isEmpty()) {
                    firstLine = l;
                    break;
                }
            }
        }
        if (firstLine.isEmpty()) {
            return "(no subject)";
        }
        return firstLine.length() > 60 ? firstLine.substring(0, 60) + "…" : firstLine;
    }

>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
        if (reputationService.shouldAutoHold(sessionHash)) {
            status = PostStatus.HELD_FOR_REVIEW;
        }
        post.setStatus(status);
=======
        post.setStatus(status);
        if (reputationService.shouldAutoHold(sessionHash)) {
            // A low-trust poster is flagged, never hidden. A person decides
            // whether anything comes down. The reason says exactly what the
            // trust score is, so a moderator does not have to guess.
            post.setFlagged(true);
            post.setFlagSource("reputation");
            String label = "the poster's browser has a low trust score ("
                    + reputationService.trustSummary(sessionHash) + ")";
            post.setFlagReason(post.getFlagReason() == null ? label : post.getFlagReason() + "; " + label);
        }
>>>>>>> d5851d3 (Initial commit)
    }

    private void storeMetadata(UUID postId, String sessionHash, String ip) {
        PosterMetadata meta = new PosterMetadata();
        meta.setId(UUID.randomUUID());
        meta.setPostId(postId);
        meta.setIpEncrypted(metadataCryptoService.encrypt(ip == null ? "unknown" : ip));
        meta.setSessionHashEncrypted(metadataCryptoService.encrypt(sessionHash));
        posterMetadataRepository.save(meta);
    }

<<<<<<< HEAD
=======
    private void recordPostSession(UUID postId, String sessionHash) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return;
        }
        PostSession ps = new PostSession();
        ps.setId(UUID.randomUUID());
        ps.setPostId(postId);
        ps.setSessionHash(sessionHash);
        postSessionRepository.save(ps);
    }

    /** Posts created by this anonymous session, newest first. */
    @Transactional(readOnly = true)
    public List<Post> listMyPosts(String sessionHash) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return List.of();
        }
        List<Post> posts = new ArrayList<>();
        for (PostSession ps : postSessionRepository.findBySessionHashOrderByCreatedAtDesc(sessionHash)) {
            postRepository.findById(ps.getPostId()).ifPresent(posts::add);
        }
        return posts;
    }

    /**
     * The original poster can bump their own thread back to the top, the way a
     * 4chan OP does. Only the session that started the thread may do this.
     */
    @Transactional
    public boolean bumpOwnThread(UUID threadId, String sessionHash) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return false;
        }
        var session = postSessionRepository.findAll().stream()
                .filter(s -> s.getPostId().equals(threadId))
                .findFirst()
                .orElse(null);
        if (session == null || !session.getSessionHash().equals(sessionHash)) {
            return false;
        }
        ThreadEntity thread = threadRepository.findById(threadId).orElse(null);
        if (thread == null) {
            return false;
        }
        thread.setBumpedAt(Instant.now());
        threadRepository.save(thread);
        return true;
    }

    /**
     * The author can delete their own post, but only within 15 minutes of
     * posting it. The post stays in the thread as a tombstone so the numbering
     * and any quotes still line up.
     */
    @Transactional
    public boolean deleteOwnPost(UUID postId, String sessionHash) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return false;
        }
        boolean mine = postSessionRepository.findAll().stream()
                .anyMatch(s -> s.getPostId().equals(postId) && s.getSessionHash().equals(sessionHash));
        if (!mine) {
            return false;
        }
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null || post.getStatus() == PostStatus.BLOCKED || post.isDeletedByAuthor()) {
            return false;
        }
        Instant deadline = post.getCreatedAt().plus(DELETE_WINDOW_MINUTES, ChronoUnit.MINUTES);
        if (Instant.now().isAfter(deadline)) {
            return false;
        }
        post.setStatus(PostStatus.BLOCKED);
        post.setDeletedByAuthor(true);
        post.setOriginalText("[deleted by the author]");
        post.setTranslatedText(null);
        postRepository.save(post);
        return true;
    }

    /** True when this session started the thread (the OP). */
    @Transactional(readOnly = true)
    public boolean isThreadOwner(UUID threadId, String sessionHash) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return false;
        }
        return postRepository.findByThreadIdOrderByCreatedAtAsc(threadId).stream()
                .findFirst()
                .flatMap(first -> postSessionRepository.findAll().stream()
                        .filter(s -> s.getPostId().equals(first.getId()))
                        .findFirst())
                .map(s -> s.getSessionHash().equals(sessionHash))
                .orElse(false);
    }

    /** The ids of the posts this session wrote, so the UI can offer edit/delete. */
    @Transactional(readOnly = true)
    public java.util.Set<UUID> myPostIds(String sessionHash) {
        java.util.Set<UUID> ids = new java.util.HashSet<>();
        if (sessionHash == null || sessionHash.isBlank()) {
            return ids;
        }
        for (PostSession s : postSessionRepository.findBySessionHashOrderByCreatedAtDesc(sessionHash)) {
            ids.add(s.getPostId());
        }
        return ids;
    }

    /**
     * The posts this session may still delete: written by them and less than
     * 15 minutes old.
     */
    @Transactional(readOnly = true)
    public java.util.Set<UUID> deletablePostIds(String sessionHash) {
        java.util.Set<UUID> ids = new java.util.HashSet<>();
        if (sessionHash == null || sessionHash.isBlank()) {
            return ids;
        }
        Instant cutoff = Instant.now().minus(DELETE_WINDOW_MINUTES, ChronoUnit.MINUTES);
        for (PostSession s : postSessionRepository.findBySessionHashOrderByCreatedAtDesc(sessionHash)) {
            Post p = postRepository.findById(s.getPostId()).orElse(null);
            if (p != null && p.getCreatedAt().isAfter(cutoff)
                    && p.getStatus() != PostStatus.BLOCKED
                    && !p.isDeletedByAuthor()) {
                ids.add(p.getId());
            }
        }
        return ids;
    }

    /**
     * Finds a very similar recent post in the same board, so the poster can be
     * warned. Nothing is blocked.
     */
    @Transactional(readOnly = true)
    public String findDuplicate(UUID boardId, String text) {
        return duplicateDetector.findSimilar(boardId, text);
    }

    /** Every released image in a thread, for the gallery view. */
    @Transactional(readOnly = true)
    public List<com.schoolchan.media.MediaEntity> listThreadMedia(UUID threadId) {
        List<com.schoolchan.media.MediaEntity> out = new ArrayList<>();
        for (Post p : postRepository.findByThreadIdOrderByCreatedAtAsc(threadId)) {
            if (p.getStatus() != PostStatus.PUBLISHED) {
                continue;
            }
            for (var m : mediaService.findByPost(p.getId())) {
                if ("RELEASED".equals(m.getQuarantineStatus())) {
                    out.add(m);
                }
            }
        }
        return out;
    }

    /**
     * Edits a post within the edit window, and only for the session that
     * created it. The original text is replaced; moderation is re-run so the
     * edit is screened like a new post.
     */
    @Transactional
    public boolean editPost(UUID postId, String sessionHash, String newText) {
        if (sessionHash == null || sessionHash.isBlank() || newText == null || newText.isBlank()) {
            return false;
        }
        if (newText.length() > 8000) {
            return false;
        }
        var session = postSessionRepository.findAll().stream()
                .filter(s -> s.getPostId().equals(postId))
                .findFirst()
                .orElse(null);
        if (session == null || !session.getSessionHash().equals(sessionHash)) {
            return false;
        }
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return false;
        }
        if (post.getStatus() == PostStatus.BLOCKED) {
            return false;
        }
        Instant deadline = post.getCreatedAt().plus(editWindowMinutes, ChronoUnit.MINUTES);
        if (Instant.now().isAfter(deadline)) {
            return false;
        }
        SpoilerParser.Result parsed = SpoilerParser.parse(newText);
        post.setOriginalText(parsed.text());
        post.setSpoiler(parsed.spoiler());
        post.setSpoilerLabel(parsed.label());
        post.setStatus(PostStatus.PENDING_SCREENING);
        postRepository.save(post);
        applyModeration(post, sessionHash);
        postRepository.save(post);
        releaseIfPublished(post);
        return true;
    }

>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
=======

    private void scheduleTranslation(Post post) {
        if (post.getTranslatedText() != null && !post.getTranslatedText().isBlank()) {
            return;
        }
        final UUID postId = post.getId();
        final String original = post.getOriginalText();
        translationExecutor.submit(() -> {
            try {
                translationClient.translate(original).ifPresent(tr -> {
                    postRepository.findById(postId).ifPresent(p -> {
                        p.setTranslatedText(tr.translatedText());
                        p.setDetectedLanguage(tr.detectedLanguage());
                        p.setTranslationModel(tr.modelId());
                        postRepository.save(p);
                    });
                });
            } catch (Exception ignored) {
            }
        });
    }
>>>>>>> d5851d3 (Initial commit)
}
