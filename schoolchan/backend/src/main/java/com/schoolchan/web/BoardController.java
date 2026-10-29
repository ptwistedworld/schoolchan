package com.schoolchan.web;

import com.schoolchan.board.BoardService;
<<<<<<< HEAD
import com.schoolchan.board.Post;
import com.schoolchan.board.PostFormatter;
import com.schoolchan.board.PostRepository;
=======
import com.schoolchan.board.BookmarkService;
import com.schoolchan.board.CatalogOrder;
import com.schoolchan.board.Post;
import com.schoolchan.board.PostFormatter;
import com.schoolchan.board.PostStatus;
import com.schoolchan.board.SiteSettingRepository;
import com.schoolchan.board.QuoteIndex;
import com.schoolchan.board.QuoteInboxService;
import com.schoolchan.board.ThreadPreview;
import com.schoolchan.board.ThreadRepository;
import com.schoolchan.board.PostRepository;
import com.schoolchan.config.SchoolChanProperties;
>>>>>>> d5851d3 (Initial commit)
import com.schoolchan.media.MediaService;
import com.schoolchan.reputation.ReportService;
import com.schoolchan.session.AnonymousSessionFilter;
import jakarta.servlet.http.HttpServletRequest;
<<<<<<< HEAD
=======
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
>>>>>>> d5851d3 (Initial commit)
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
<<<<<<< HEAD
import org.springframework.web.multipart.MultipartFile;

=======
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
>>>>>>> d5851d3 (Initial commit)
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class BoardController {

    private final BoardService boardService;
    private final PostFormatter postFormatter;
    private final ReportService reportService;
    private final PostRepository postRepository;
    private final MediaService mediaService;
<<<<<<< HEAD
=======
    private final ThreadRepository threadRepository;
    private final BookmarkService bookmarkService;
    private final com.schoolchan.board.ThreadWatchService watchService;
    private final com.schoolchan.board.AppealService appealService;
    private final com.schoolchan.staff.StaffNoteRepository staffNoteRepository;
    private final com.schoolchan.student.StudentAccessPolicy studentAccessPolicy;
    private final com.schoolchan.board.BoardPageService boardPageService;
    private final SiteSettingRepository siteSettingRepository;
    private final QuoteInboxService quoteInboxService;
    private final SchoolChanProperties properties;
>>>>>>> d5851d3 (Initial commit)

    public BoardController(
            BoardService boardService,
            PostFormatter postFormatter,
            ReportService reportService,
            PostRepository postRepository,
<<<<<<< HEAD
            MediaService mediaService) {
=======
            MediaService mediaService,
            ThreadRepository threadRepository,
            BookmarkService bookmarkService,
            com.schoolchan.board.ThreadWatchService watchService,
            com.schoolchan.board.AppealService appealService,
            com.schoolchan.staff.StaffNoteRepository staffNoteRepository,
            com.schoolchan.student.StudentAccessPolicy studentAccessPolicy,
            com.schoolchan.board.BoardPageService boardPageService,
            SiteSettingRepository siteSettingRepository,
            QuoteInboxService quoteInboxService,
            SchoolChanProperties properties) {
>>>>>>> d5851d3 (Initial commit)
        this.boardService = boardService;
        this.postFormatter = postFormatter;
        this.reportService = reportService;
        this.postRepository = postRepository;
        this.mediaService = mediaService;
<<<<<<< HEAD
    }

    @GetMapping("/boards/{slug}")
    public String board(@PathVariable String slug, @RequestParam(required = false) String notice, Model model) {
        var board = boardService.requireBoard(slug);
        model.addAttribute("board", board);
        model.addAttribute("threads", boardService.listActiveThreads(board.getId()));
        model.addAttribute("notice", noticeText(notice));
=======
        this.threadRepository = threadRepository;
        this.bookmarkService = bookmarkService;
        this.watchService = watchService;
        this.appealService = appealService;
        this.staffNoteRepository = staffNoteRepository;
        this.studentAccessPolicy = studentAccessPolicy;
        this.boardPageService = boardPageService;
        this.siteSettingRepository = siteSettingRepository;
        this.quoteInboxService = quoteInboxService;
        this.properties = properties;
    }

    /**
     * "My posts" and "Bookmarks" are tied to a browser. They are only worth
     * showing to somebody who can actually post, so when the student gate is
     * on, a signed-out visitor is sent to the sign-in page instead.
     */
    private boolean canSeePersonalPages() {
        if (isStaff() || isStudent()) {
            return true;
        }
        return !studentAccessPolicy.loginRequired();
    }

    private boolean isStudent() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getAuthorities().stream().anyMatch(a -> "ROLE_STUDENT".equals(a.getAuthority()));
    }

    @GetMapping("/boards/{slug}")
    public String board(@PathVariable String slug, @RequestParam(required = false) String notice,
                        @RequestParam(defaultValue = "0") int page, HttpServletRequest request,
                        RedirectAttributes redirectAttributes, Model model) {
        var board = boardService.requireBoard(slug);
        var all = boardService.listActiveThreads(board.getId());
        int size = 20;
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        var slice = all.subList(from, to);
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        // One query for the opening posts, one for the pictures, one for the
        // bookmarks, instead of three per thread.
        var pageData = boardPageService.load(
                slice.stream().map(com.schoolchan.board.ThreadEntity::getId).toList(), sessionHash);

        model.addAttribute("board", board);
        model.addAttribute("threads", slice);
        model.addAttribute("postFormatter", postFormatter);
        model.addAttribute("bookmarked", pageData.bookmarked());
        model.addAttribute("firstPost", pageData.firstPost());
        model.addAttribute("previewImage", pageData.previewImage());
        model.addAttribute("page", page);
        model.addAttribute("totalPages", (int) Math.ceil(all.size() / (double) size));
        model.addAttribute("notice", noticeText(resolveNotice(notice, redirectAttributes)));
>>>>>>> d5851d3 (Initial commit)
        model.addAttribute("pageTitle", board.getName() + " — SchoolChan");
        return "board";
    }

<<<<<<< HEAD
    @GetMapping("/boards/{slug}/catalog")
    public String catalog(@PathVariable String slug, Model model) {
        var board = boardService.requireBoard(slug);
        model.addAttribute("board", board);
        model.addAttribute("threads", boardService.listActiveThreads(board.getId()));
=======
    /**
     * Returns just the thread cards for a page, used by the endless-scroll
     * loader. Same markup as the server-rendered list.
     */
    @GetMapping("/boards/{slug}/threads-fragment")
    public String threadsFragment(@PathVariable String slug,
                                  @RequestParam(defaultValue = "0") int page,
                                  HttpServletRequest request,
                                  Model model) {
        var board = boardService.requireBoard(slug);
        var all = boardService.listActiveThreads(board.getId());
        int size = 20;
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        var slice = all.subList(from, to);
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        var pageData = boardPageService.load(
                slice.stream().map(com.schoolchan.board.ThreadEntity::getId).toList(), sessionHash);

        model.addAttribute("threads", slice);
        model.addAttribute("firstPost", pageData.firstPost());
        model.addAttribute("previewImage", pageData.previewImage());
        model.addAttribute("bookmarked", pageData.bookmarked());
        model.addAttribute("board", board);
        model.addAttribute("postFormatter", postFormatter);
        model.addAttribute("bookmarked", pageData.bookmarked());
        model.addAttribute("hasMore", to < all.size());
        return "fragments/thread-list-fragment";
    }

    @GetMapping("/boards/{slug}/catalog")
    public String catalog(@PathVariable String slug,
                          @RequestParam(defaultValue = "bump") String sort,
                          @RequestParam(name = "images", defaultValue = "false") boolean imagesOnly,
                          HttpServletRequest request, Model model) {
        var board = boardService.requireBoard(slug);
        String catalogSort = CatalogOrder.normalize(sort);
        var threads = boardService.listActiveThreads(board.getId());
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        var pageData = boardPageService.load(
                threads.stream().map(com.schoolchan.board.ThreadEntity::getId).toList(), sessionHash);
        // "Has pictures" means the thread has a released image anywhere in it,
        // not just a nonzero counter: the preview query is the same one the
        // catalog already uses for the thumbnails.
        if (imagesOnly) {
            threads.removeIf(t -> !pageData.previewImage().containsKey(t.getId()));
        }
        threads = CatalogOrder.sorted(threads, catalogSort);

        model.addAttribute("board", board);
        model.addAttribute("threads", threads);
        model.addAttribute("firstPost", pageData.firstPost());
        model.addAttribute("previewImage", pageData.previewImage());
        model.addAttribute("catalogSort", catalogSort);
        model.addAttribute("imagesOnly", imagesOnly);
>>>>>>> d5851d3 (Initial commit)
        model.addAttribute("pageTitle", board.getName() + " catalog — SchoolChan");
        return "catalog";
    }

    @GetMapping("/boards/{slug}/archive")
<<<<<<< HEAD
    public String archive(@PathVariable String slug, Model model) {
        var board = boardService.requireBoard(slug);
        model.addAttribute("board", board);
        model.addAttribute("threads", boardService.listArchivedThreads(board.getId()));
=======
    public String archive(@PathVariable String slug, @RequestParam(defaultValue = "0") int page, Model model) {
        var board = boardService.requireBoard(slug);
        var all = boardService.listArchivedThreads(board.getId());
        int size = 20;
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        model.addAttribute("board", board);
        model.addAttribute("threads", all.subList(from, to));
        model.addAttribute("page", page);
        model.addAttribute("totalPages", (int) Math.ceil(all.size() / (double) size));
>>>>>>> d5851d3 (Initial commit)
        model.addAttribute("pageTitle", "Archive — " + board.getName());
        return "archive";
    }

    @GetMapping("/boards/{slug}/threads/{threadId}")
    public String thread(
            @PathVariable String slug,
            @PathVariable UUID threadId,
            @RequestParam(required = false) String notice,
<<<<<<< HEAD
            Model model) {
        var board = boardService.requireBoard(slug);
        var posts = boardService.listPosts(threadId);
        model.addAttribute("board", board);
        model.addAttribute("threadId", threadId);
        model.addAttribute("posts", posts);
        model.addAttribute("formatter", postFormatter);
        model.addAttribute("notice", noticeText(notice));
=======
            @RequestParam(name = "all", defaultValue = "false") boolean all,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model) {
        var board = boardService.requireBoard(slug);
        var posts = boardService.listPosts(threadId);
        var allPosts = boardService.listPostsIncludingHidden(threadId);
        // A long thread shows its opening post and the newest replies; the rest
        // are one click away. The quote graph is built from the full list so a
        // visible post can still show who answered it.
        int limit = properties.getThreads().getReplyPreviewLimit();
        ThreadPreview preview = ThreadPreview.of(allPosts, limit);
        boolean canCollapse = preview.omitted() > 0;
        if (all) {
            preview = ThreadPreview.of(allPosts, 0);
        }
        var thread = threadRepository.findById(threadId).orElse(null);
        model.addAttribute("board", board);
        model.addAttribute("threadId", threadId);
        model.addAttribute("threadSubject", thread == null ? null : thread.getSubject());
        model.addAttribute("imageCount", thread == null ? 0 : thread.getImageCount());
        model.addAttribute("imageLimit", board.getImageLimit());
        model.addAttribute("bumpLimit", board.getBumpLimit());
        model.addAttribute("bumpLimitReached", thread != null && thread.getReplyCount() >= board.getBumpLimit());
        model.addAttribute("lastPostNumber", posts.isEmpty() ? 0 : posts.get(posts.size() - 1).getBoardPostNumber());
        model.addAttribute("allPosts", preview.visible());
        model.addAttribute("omittedCount", preview.omitted());
        model.addAttribute("showingAll", all && canCollapse);
        model.addAttribute("backlinks", QuoteIndex.of(allPosts));
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        model.addAttribute("watching", watchService.isWatching(sessionHash, threadId));
        model.addAttribute("unreadCount", watchService.unreadCount(sessionHash, threadId));
        model.addAttribute("isOp", boardService.isThreadOwner(threadId, sessionHash));
        model.addAttribute("myPostIds", boardService.myPostIds(sessionHash));
        model.addAttribute("deletablePostIds", boardService.deletablePostIds(sessionHash));
        // Internal staff notes are only ever attached to the model for staff.
        if (isStaff()) {
            model.addAttribute("staffNotes", staffNoteRepository.findByThreadIdOrderByCreatedAtDesc(threadId));
        }
        model.addAttribute("threadBookmarked",
                bookmarkService.isBookmarked((String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR), threadId));
        model.addAttribute("posts", posts);
        model.addAttribute("formatter", postFormatter);
        model.addAttribute("notice", noticeText(resolveNotice(notice, redirectAttributes)));
>>>>>>> d5851d3 (Initial commit)
        model.addAttribute(
                "mediaByPost",
                posts.stream().collect(Collectors.toMap(Post::getId, p -> mediaService.findByPost(p.getId()))));
        model.addAttribute("pageTitle", "Thread — " + board.getName());
<<<<<<< HEAD
        return "thread";
    }

=======
        model.addAttribute("livePollSeconds", properties.getThread().getLivePollSeconds());
        model.addAttribute("livePollEnabled", siteSettingRepository.findById("live_poll_enabled")
                .map(s -> "true".equalsIgnoreCase(s.getValue())).orElse(true));
        return "thread";
    }

    /**
     * Posts that arrived after {@code after}, rendered with the same markup as
     * the thread page so the live-poll bar can drop them in unchanged. Only
     * published posts are returned: a held or removed post never appears live.
     */
    @GetMapping("/boards/{slug}/threads/{threadId}/posts-after/{after}")
    public String postsAfter(@PathVariable String slug,
                             @PathVariable UUID threadId,
                             @PathVariable long after,
                             HttpServletRequest request,
                             Model model) {
        var board = boardService.requireBoard(slug);
        var posts = boardService.listPosts(threadId).stream()
                .filter(p -> p.getBoardPostNumber() > after)
                .toList();
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        var mediaByPost = posts.stream().collect(Collectors.toMap(
                Post::getId, p -> mediaService.findByPost(p.getId()), (a, b) -> a));
        model.addAttribute("board", board);
        model.addAttribute("threadId", threadId);
        model.addAttribute("posts", posts);
        model.addAttribute("backlinks", QuoteIndex.of(posts));
        model.addAttribute("myPostIds", boardService.myPostIds(sessionHash));
        model.addAttribute("deletablePostIds", boardService.deletablePostIds(sessionHash));
        model.addAttribute("mediaByPost", mediaByPost);
        model.addAttribute("formatter", postFormatter);
        return "fragments/posts";
    }

    /**
     * A short, stable link to one post. The thread URL carries the thread id and
     * a fragment; this route only needs the board and the post number, so it is
     * the one to paste into a group chat.
     */
    @GetMapping("/boards/{slug}/p/{number}")
    public String postPermalink(@PathVariable String slug, @PathVariable long number) {
        var board = boardService.requireBoard(slug);
        var post = postRepository.findByBoardIdAndBoardPostNumber(board.getId(), number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return "redirect:/boards/" + slug + "/threads/" + post.getThreadId() + "#p" + number;
    }

>>>>>>> d5851d3 (Initial commit)
    @GetMapping("/boards/{slug}/preview/{number}")
    public String preview(@PathVariable String slug, @PathVariable long number, Model model) {
        var board = boardService.requireBoard(slug);
        postRepository.findByBoardIdAndBoardPostNumber(board.getId(), number).ifPresent(post -> {
<<<<<<< HEAD
            model.addAttribute("number", number);
            model.addAttribute("html", postFormatter.format(post));
=======
            if (post.getStatus() != PostStatus.PUBLISHED) {
                return;
            }
            model.addAttribute("number", number);
            model.addAttribute("html", postFormatter.format(post));
            model.addAttribute("spoiler", post.isSpoiler());
            model.addAttribute("spoilerLabel", post.getSpoilerLabel());
            // Hovering a quoted post shows the post's own picture, so a reply
            // that carried an attachment can be recognised without opening it.
            model.addAttribute("previewImage", previewName(mediaService.findByPost(post.getId())));
>>>>>>> d5851d3 (Initial commit)
        });
        return "fragments/preview";
    }

<<<<<<< HEAD
=======
    @GetMapping("/threads/{threadId}/preview")
    public String threadPreview(@PathVariable UUID threadId, Model model) {
        var posts = boardService.listPosts(threadId);
        if (!posts.isEmpty()) {
            model.addAttribute("number", posts.get(0).getBoardPostNumber());
            model.addAttribute("html", postFormatter.format(posts.get(0)));
            model.addAttribute("spoiler", posts.get(0).isSpoiler());
            model.addAttribute("spoilerLabel", posts.get(0).getSpoilerLabel());
            // The picture may be on the opening post or on a reply; show the
            // same one the thread card uses.
            var picture = boardPageService.load(List.of(threadId), null).previewImage().get(threadId);
            if (picture != null) {
                model.addAttribute("previewImage",
                        picture.getThumbnailName() != null ? picture.getThumbnailName() : picture.getStoredName());
            }
        }
        return "fragments/preview";
    }

    /** The small copy of the first released image, or null when there is none. */
    private String previewName(List<com.schoolchan.media.MediaEntity> media) {
        if (media == null) {
            return null;
        }
        return media.stream()
                .filter(m -> "RELEASED".equals(m.getQuarantineStatus()))
                .filter(m -> m.getContentType() != null && m.getContentType().startsWith("image/"))
                .findFirst()
                .map(m -> m.getThumbnailName() != null ? m.getThumbnailName() : m.getStoredName())
                .orElse(null);
    }

>>>>>>> d5851d3 (Initial commit)
    @GetMapping("/posts/{postId}/translation")
    public String translation(@PathVariable UUID postId, Model model) {
        Post post = postRepository.findById(postId).orElseThrow();
        model.addAttribute("text", post.getTranslatedText() == null ? "" : postFormatter.formatPlain(post.getTranslatedText()));
        model.addAttribute("language", post.getDetectedLanguage());
        return "fragments/translation";
    }

<<<<<<< HEAD
=======
    /** True when the post has a translation that differs from the original. */
    public static boolean hasTranslation(Post post) {
        if (post == null) {
            return false;
        }
        String t = post.getTranslatedText();
        if (t == null || t.isBlank()) {
            return false;
        }
        String lang = post.getDetectedLanguage();
        if (lang == null || lang.isBlank() || "en".equalsIgnoreCase(lang)) {
            return false;
        }
        return !t.equals(post.getOriginalText());
    }

    @PostMapping("/bookmarks/{threadId}/toggle")
    @ResponseBody
    public java.util.Map<String, Object> toggleBookmark(@PathVariable UUID threadId, HttpServletRequest request) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        boolean nowBookmarked = bookmarkService.toggle(sessionHash, threadId);
        return java.util.Map.of("bookmarked", nowBookmarked);
    }

    @GetMapping("/my-posts")
    public String myPosts(@RequestParam(required = false) String notice,
                          @RequestParam(name = "tab", defaultValue = "posts") String tab,
                          HttpServletRequest request,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (!canSeePersonalPages()) {
            return "redirect:/student/login?next="
                    + java.net.URLEncoder.encode("/my-posts", java.nio.charset.StandardCharsets.UTF_8);
        }
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        List<Post> posts = boardService.listMyPosts(sessionHash);
        // A post is only hidden here when a staff member took it down or it was
        // held for a safety concern. A flagged post is still published and is
        // not announced to the person who wrote it.
        java.util.Map<java.util.UUID, String> reasons = new java.util.HashMap<>();
        for (Post p : posts) {
            if (p.getStatus() != PostStatus.HELD_FOR_REVIEW) {
                continue;
            }
            reasons.put(p.getId(), "A staff member is looking at this post because it may be a safety "
                    + "concern. It is hidden until they finish. It is not a punishment.");
        }
        boolean quotesView = "quotes".equals(tab);
        model.addAttribute("view", quotesView ? "quotes" : "posts");
        model.addAttribute("quotes", quotesView ? quoteInboxService.forSession(sessionHash) : List.of());
        model.addAttribute("myPostIds", boardService.myPostIds(sessionHash));
        model.addAttribute("deletablePostIds", boardService.deletablePostIds(sessionHash));
        model.addAttribute("posts", posts);
        model.addAttribute("holdReasons", reasons);
        model.addAttribute("formatter", postFormatter);
        model.addAttribute("notice", noticeText(resolveNotice(notice, redirectAttributes)));
        model.addAttribute("pageTitle", quotesView ? "Quoted you — SchoolChan" : "My posts — SchoolChan");
        return "my-posts";
    }

    /** The OP can bump their own thread back to the top. */
    @PostMapping("/threads/{threadId}/bump")
    public String bumpThread(@PathVariable UUID threadId,
                              @RequestParam(required = false) String redirect,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        boolean ok = boardService.bumpOwnThread(threadId, sessionHash);
        redirectAttributes.addFlashAttribute("notice", ok ? "bumped" : "bump-failed");
        return "redirect:" + withNotice(safeRedirect(redirect == null || redirect.isBlank()
                ? "/boards/general" : redirect), ok ? "bumped" : "bump-failed");
    }

    /** The author can delete their own post. */
    @PostMapping("/posts/{postId}/delete")
    public String deletePost(@PathVariable UUID postId,
                             @RequestParam(required = false) String redirect,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        boolean ok = boardService.deleteOwnPost(postId, sessionHash);
        redirectAttributes.addFlashAttribute("notice", ok ? "deleted" : "delete-failed");
        return "redirect:" + withNotice(safeRedirect(redirect == null || redirect.isBlank()
                ? "/my-posts" : redirect), ok ? "deleted" : "delete-failed");
    }

    /** Ask a moderator to look at a held post again. */
    @PostMapping("/posts/{postId}/appeal")
    public String appealPost(@PathVariable UUID postId,
                             @RequestParam(required = false) String reason,
                             @RequestParam(required = false) String redirect,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        appealService.create(postId, sessionHash, reason);
        redirectAttributes.addFlashAttribute("notice", "appealed");
        return "redirect:" + withNotice(safeRedirect(redirect == null || redirect.isBlank()
                ? "/my-posts" : redirect), "appealed");
    }

    /** Watch a thread and see how many replies you have not read. */
    @PostMapping("/threads/{threadId}/watch")
    public String watchThread(@PathVariable UUID threadId,
                              @RequestParam(required = false) String redirect,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        boolean watching = watchService.toggle(sessionHash, threadId);
        redirectAttributes.addFlashAttribute("notice", watching ? "watching" : "not-watching");
        return "redirect:" + withNotice(safeRedirect(redirect == null || redirect.isBlank()
                ? "/boards/general" : redirect), watching ? "watching" : "not-watching");
    }

    /** Every image in one thread, as a gallery. */
    @GetMapping("/boards/{slug}/threads/{threadId}/gallery")
    public String gallery(@PathVariable String slug,
                          @PathVariable UUID threadId,
                          Model model) {
        var board = boardService.requireBoard(slug);
        model.addAttribute("board", board);
        model.addAttribute("threadId", threadId);
        model.addAttribute("media", boardService.listThreadMedia(threadId));
        model.addAttribute("pageTitle", "Gallery — SchoolChan");
        return "gallery";
    }

    @PostMapping("/posts/{postId}/edit")
    public String editPost(@PathVariable UUID postId,
                           @RequestParam String text,
                           @RequestParam(required = false) String redirect,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        boolean ok = boardService.editPost(postId, sessionHash, text);
        redirectAttributes.addFlashAttribute("notice", ok ? "edited" : "edit-failed");
        return "redirect:" + withNotice(safeRedirect(redirect == null || redirect.isBlank() ? "/my-posts" : redirect),
                ok ? "edited" : "edit-failed");
    }

    @GetMapping("/bookmarks")
    public String bookmarks(HttpServletRequest request, Model model) {
        if (!canSeePersonalPages()) {
            return "redirect:/student/login?next="
                    + java.net.URLEncoder.encode("/bookmarks", java.nio.charset.StandardCharsets.UTF_8);
        }
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        model.addAttribute("bookmarks", bookmarkService.listFor(sessionHash));
        model.addAttribute("pageTitle", "Bookmarks — SchoolChan");
        return "bookmarks";
    }

    @PostMapping("/bookmarks/clear")
    public String clearBookmarks(HttpServletRequest request) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        bookmarkService.clear(sessionHash);
        return "redirect:/bookmarks";
    }

>>>>>>> d5851d3 (Initial commit)
    @PostMapping("/boards/{slug}/threads")
    public String createThread(
            @PathVariable String slug,
            @RequestParam(required = false) String subject,
            @RequestParam String text,
            @RequestParam(defaultValue = "false") boolean sage,
            @RequestParam(defaultValue = "false") boolean spoiler,
<<<<<<< HEAD
            @RequestParam(required = false) MultipartFile file,
            HttpServletRequest request) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        Post post = boardService.createThread(slug, subject, text, sage, spoiler, sessionHash, request.getRemoteAddr(), file);
        return "redirect:/boards/" + slug + "?notice=" + noticeCode(post);
=======
            @RequestParam(required = false) String website,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) MultipartFile file,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        if (website != null && !website.isBlank()) {
            redirectAttributes.addFlashAttribute("notice", "posted");
            return "redirect:/boards/" + slug;
        }
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        // A post with no words is not a post. This should never happen from the
        // form, but an empty body must never reach the board.
        if (text == null || text.isBlank()) {
            redirectAttributes.addFlashAttribute("notice", "empty");
            return "redirect:/boards/" + slug;
        }
        // Warn (do not block) if this looks like a repeat.
        String duplicate = boardService.findDuplicate(boardService.requireBoard(slug).getId(), text);
        if (duplicate != null) {
            redirectAttributes.addFlashAttribute("notice", "duplicate");
            redirectAttributes.addFlashAttribute("duplicateOf", duplicate);
            return "redirect:/boards/" + slug;
        }
        Post post = boardService.createThread(slug, subject, text, sage, spoiler, sessionHash, request.getRemoteAddr(), file, tag);
        redirectAttributes.addFlashAttribute("notice", noticeCode(post));
        // Land on the post that was just made, not on the top of the board.
        return "redirect:/boards/" + slug + "/threads/" + post.getThreadId()
                + "?notice=" + noticeCode(post) + "#p" + post.getBoardPostNumber();
>>>>>>> d5851d3 (Initial commit)
    }

    @PostMapping("/boards/{slug}/threads/{threadId}/reply")
    public String reply(
            @PathVariable String slug,
            @PathVariable UUID threadId,
            @RequestParam String text,
            @RequestParam(defaultValue = "false") boolean sage,
            @RequestParam(defaultValue = "false") boolean spoiler,
<<<<<<< HEAD
            @RequestParam(required = false) MultipartFile file,
            HttpServletRequest request) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        Post post = boardService.reply(slug, threadId, text, sage, spoiler, sessionHash, request.getRemoteAddr(), file);
        return "redirect:/boards/" + slug + "/threads/" + threadId + "?notice=" + noticeCode(post);
=======
            @RequestParam(required = false) String website,
            @RequestParam(required = false) MultipartFile file,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        if (website != null && !website.isBlank()) {
            redirectAttributes.addFlashAttribute("notice", "posted");
            return "redirect:/boards/" + slug + "/threads/" + threadId;
        }
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        // A reply with no words is not a reply.
        if (text == null || text.isBlank()) {
            redirectAttributes.addFlashAttribute("notice", "empty");
            return "redirect:/boards/" + slug + "/threads/" + threadId;
        }
        Post post = boardService.reply(slug, threadId, text, sage, spoiler, sessionHash, request.getRemoteAddr(), file);
        redirectAttributes.addFlashAttribute("notice", noticeCode(post));
        // Land on the new reply so the writer sees it in context.
        return "redirect:/boards/" + slug + "/threads/" + threadId
                + "?notice=" + noticeCode(post) + "#p" + post.getBoardPostNumber();
>>>>>>> d5851d3 (Initial commit)
    }

    @PostMapping("/posts/{postId}/vote")
    public String vote(
            @PathVariable UUID postId,
            @RequestParam short value,
<<<<<<< HEAD
            @RequestParam String redirect,
            HttpServletRequest request) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        boardService.vote(postId, sessionHash, value);
        return "redirect:" + redirect;
=======
            @RequestParam(required = false) String redirect,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        boolean counted = boardService.vote(postId, sessionHash, value);
        redirectAttributes.addFlashAttribute("notice", counted ? "voted" : "already-voted");
        String target = redirect == null || redirect.isBlank() ? "/my-posts" : safeRedirect(redirect);
        return "redirect:" + withNotice(target, counted ? "voted" : "already-voted");
>>>>>>> d5851d3 (Initial commit)
    }

    @PostMapping("/posts/{postId}/report")
    public String report(
            @PathVariable UUID postId,
            @RequestParam String reason,
            @RequestParam(required = false) String note,
<<<<<<< HEAD
            @RequestParam String redirect,
            HttpServletRequest request) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        reportService.report(postId, sessionHash, reason, note);
        return "redirect:" + redirect;
    }

    private String noticeCode(Post post) {
        return switch (post.getStatus()) {
            case BLOCKED -> "blocked";
            case HELD_FOR_REVIEW -> "held";
            default -> "posted";
        };
=======
            @RequestParam(required = false) String redirect,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        boolean accepted = reportService.report(postId, sessionHash, reason, note);
        redirectAttributes.addFlashAttribute("notice", accepted ? "reported" : "already-reported");
        String target = redirect == null || redirect.isBlank() ? "/my-posts" : safeRedirect(redirect);
        return "redirect:" + withNotice(target, accepted ? "reported" : "already-reported");
    }

    /** Appends a notice code to a relative redirect path. */
    private String withNotice(String path, String code) {
        return path + (path.contains("?") ? "&" : "?") + "notice=" + code;
    }

    /** Keeps only the path of a redirect target so we never build a bad URL. */
    private String safeRedirect(String redirect) {
        if (redirect == null || redirect.isBlank()) {
            return "/";
        }
        String target = redirect;
        int scheme = target.indexOf("://");
        if (scheme >= 0) {
            int slash = target.indexOf('/', scheme + 3);
            target = slash >= 0 ? target.substring(slash) : "/";
        }
        int query = target.indexOf('?');
        if (query >= 0) {
            target = target.substring(0, query);
        }
        if (!target.startsWith("/")) {
            target = "/" + target;
        }
        return target;
    }

    private String noticeCode(Post post) {
        if (post.getStatus() == PostStatus.BLOCKED) {
            return "blocked";
        }
        if (post.getStatus() == PostStatus.HELD_FOR_REVIEW) {
            return "held";
        }
        // A flagged post is published like any other. The person who wrote it
        // is not told about it; only staff see the flag.
        return "posted";
    }

    private boolean isStaff() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        // A signed-in student is not staff.
        return auth.getAuthorities().stream().anyMatch(a ->
                "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_MODERATOR".equals(a.getAuthority()));
    }

    private String resolveNotice(String requestParam, RedirectAttributes redirectAttributes) {
        Object flash = redirectAttributes.getFlashAttributes().get("notice");
        if (flash != null) {
            return flash.toString();
        }
        return requestParam;
>>>>>>> d5851d3 (Initial commit)
    }

    private String noticeText(String code) {
        if (code == null) {
            return null;
        }
        return switch (code) {
<<<<<<< HEAD
            case "blocked" -> "That post was not published. It did not meet the board rules.";
            case "held" -> "That post is waiting for a moderator. It is not visible to other students yet.";
            case "posted" -> "Posted.";
=======
            case "blocked" -> "A moderator removed that post. It did not meet the board rules.";
            case "held" -> "That post is hidden while a staff member looks at it, because it may be a safety concern. It is not a punishment.";
            case "posted" -> "Posted.";
            case "empty" -> "There was no message to post. Write something and try again.";
            case "voted" -> "Vote counted.";
            case "already-voted" -> "You already voted on this post.";
            case "reported" -> "Report sent to the moderation team. Thank you.";
            case "already-reported" -> "You already reported this post.";
            case "edited" -> "Post updated and re-screened.";
            case "edit-failed" -> "That post can no longer be edited (only the author, within 10 minutes).";
            case "deleted" -> "Post deleted.";
            case "delete-failed" -> "Only the author can delete a post, and only within 15 minutes of posting it.";
            case "appealed" -> "Thanks. A moderator will take another look.";
            case "bumped" -> "Thread bumped back to the top.";
            case "bump-failed" -> "Only the person who started the thread can bump it.";
            case "watching" -> "You are now watching this thread.";
            case "not-watching" -> "You stopped watching this thread.";
            case "duplicate" -> "That looks like something already posted. It was not posted - have another look.";
>>>>>>> d5851d3 (Initial commit)
            default -> null;
        };
    }
}
