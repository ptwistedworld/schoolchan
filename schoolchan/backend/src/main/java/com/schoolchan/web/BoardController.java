package com.schoolchan.web;

import com.schoolchan.board.BoardService;
import com.schoolchan.board.Post;
import com.schoolchan.board.PostFormatter;
import com.schoolchan.board.PostRepository;
import com.schoolchan.media.MediaService;
import com.schoolchan.reputation.ReportService;
import com.schoolchan.session.AnonymousSessionFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class BoardController {

    private final BoardService boardService;
    private final PostFormatter postFormatter;
    private final ReportService reportService;
    private final PostRepository postRepository;
    private final MediaService mediaService;

    public BoardController(
            BoardService boardService,
            PostFormatter postFormatter,
            ReportService reportService,
            PostRepository postRepository,
            MediaService mediaService) {
        this.boardService = boardService;
        this.postFormatter = postFormatter;
        this.reportService = reportService;
        this.postRepository = postRepository;
        this.mediaService = mediaService;
    }

    @GetMapping("/boards/{slug}")
    public String board(@PathVariable String slug, @RequestParam(required = false) String notice, Model model) {
        var board = boardService.requireBoard(slug);
        model.addAttribute("board", board);
        model.addAttribute("threads", boardService.listActiveThreads(board.getId()));
        model.addAttribute("notice", noticeText(notice));
        model.addAttribute("pageTitle", board.getName() + " — SchoolChan");
        return "board";
    }

    @GetMapping("/boards/{slug}/catalog")
    public String catalog(@PathVariable String slug, Model model) {
        var board = boardService.requireBoard(slug);
        model.addAttribute("board", board);
        model.addAttribute("threads", boardService.listActiveThreads(board.getId()));
        model.addAttribute("pageTitle", board.getName() + " catalog — SchoolChan");
        return "catalog";
    }

    @GetMapping("/boards/{slug}/archive")
    public String archive(@PathVariable String slug, Model model) {
        var board = boardService.requireBoard(slug);
        model.addAttribute("board", board);
        model.addAttribute("threads", boardService.listArchivedThreads(board.getId()));
        model.addAttribute("pageTitle", "Archive — " + board.getName());
        return "archive";
    }

    @GetMapping("/boards/{slug}/threads/{threadId}")
    public String thread(
            @PathVariable String slug,
            @PathVariable UUID threadId,
            @RequestParam(required = false) String notice,
            Model model) {
        var board = boardService.requireBoard(slug);
        var posts = boardService.listPosts(threadId);
        model.addAttribute("board", board);
        model.addAttribute("threadId", threadId);
        model.addAttribute("posts", posts);
        model.addAttribute("formatter", postFormatter);
        model.addAttribute("notice", noticeText(notice));
        model.addAttribute(
                "mediaByPost",
                posts.stream().collect(Collectors.toMap(Post::getId, p -> mediaService.findByPost(p.getId()))));
        model.addAttribute("pageTitle", "Thread — " + board.getName());
        return "thread";
    }

    @GetMapping("/boards/{slug}/preview/{number}")
    public String preview(@PathVariable String slug, @PathVariable long number, Model model) {
        var board = boardService.requireBoard(slug);
        postRepository.findByBoardIdAndBoardPostNumber(board.getId(), number).ifPresent(post -> {
            model.addAttribute("number", number);
            model.addAttribute("html", postFormatter.format(post));
        });
        return "fragments/preview";
    }

    @GetMapping("/posts/{postId}/translation")
    public String translation(@PathVariable UUID postId, Model model) {
        Post post = postRepository.findById(postId).orElseThrow();
        model.addAttribute("text", post.getTranslatedText() == null ? "" : postFormatter.formatPlain(post.getTranslatedText()));
        model.addAttribute("language", post.getDetectedLanguage());
        return "fragments/translation";
    }

    @PostMapping("/boards/{slug}/threads")
    public String createThread(
            @PathVariable String slug,
            @RequestParam(required = false) String subject,
            @RequestParam String text,
            @RequestParam(defaultValue = "false") boolean sage,
            @RequestParam(defaultValue = "false") boolean spoiler,
            @RequestParam(required = false) MultipartFile file,
            HttpServletRequest request) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        Post post = boardService.createThread(slug, subject, text, sage, spoiler, sessionHash, request.getRemoteAddr(), file);
        return "redirect:/boards/" + slug + "?notice=" + noticeCode(post);
    }

    @PostMapping("/boards/{slug}/threads/{threadId}/reply")
    public String reply(
            @PathVariable String slug,
            @PathVariable UUID threadId,
            @RequestParam String text,
            @RequestParam(defaultValue = "false") boolean sage,
            @RequestParam(defaultValue = "false") boolean spoiler,
            @RequestParam(required = false) MultipartFile file,
            HttpServletRequest request) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        Post post = boardService.reply(slug, threadId, text, sage, spoiler, sessionHash, request.getRemoteAddr(), file);
        return "redirect:/boards/" + slug + "/threads/" + threadId + "?notice=" + noticeCode(post);
    }

    @PostMapping("/posts/{postId}/vote")
    public String vote(
            @PathVariable UUID postId,
            @RequestParam short value,
            @RequestParam String redirect,
            HttpServletRequest request) {
        String sessionHash = (String) request.getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
        boardService.vote(postId, sessionHash, value);
        return "redirect:" + redirect;
    }

    @PostMapping("/posts/{postId}/report")
    public String report(
            @PathVariable UUID postId,
            @RequestParam String reason,
            @RequestParam(required = false) String note,
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
    }

    private String noticeText(String code) {
        if (code == null) {
            return null;
        }
        return switch (code) {
            case "blocked" -> "That post was not published. It did not meet the board rules.";
            case "held" -> "That post is waiting for a moderator. It is not visible to other students yet.";
            case "posted" -> "Posted.";
            default -> null;
        };
    }
}
