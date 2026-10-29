package com.schoolchan.web;

import com.schoolchan.board.Board;
import com.schoolchan.board.BoardRepository;
import com.schoolchan.board.Post;
import com.schoolchan.board.PostRepository;
import com.schoolchan.board.PostStatus;
import com.schoolchan.board.ThreadEntity;
import com.schoolchan.board.ThreadRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
public class SearchController {

    private final PostRepository postRepository;
    private final ThreadRepository threadRepository;
    private final BoardRepository boardRepository;

    public SearchController(PostRepository postRepository, ThreadRepository threadRepository, BoardRepository boardRepository) {
        this.postRepository = postRepository;
        this.threadRepository = threadRepository;
        this.boardRepository = boardRepository;
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q,
                         @RequestParam(required = false) String board,
                         @RequestParam(required = false) String since,
                         @RequestParam(required = false) String withImages,
                         @RequestParam(required = false) String minReplies,
                         Model model) {
        String query = (q == null) ? "" : q.trim();
        model.addAttribute("q", query);
        model.addAttribute("boardFilter", board);
        model.addAttribute("since", since);
        model.addAttribute("withImages", withImages);
        model.addAttribute("minReplies", minReplies);
        model.addAttribute("boards", boardRepository.findAll());
        model.addAttribute("pageTitle", "Search — SchoolChan");
        List<Map<String, Object>> results = new ArrayList<>();
        if (query.length() >= 2) {
            Set<UUID> threadIds = new LinkedHashSet<>();
            for (Post p : postRepository.findByOriginalTextContainingIgnoreCase(query)) {
                if (p.getStatus() == PostStatus.PUBLISHED) {
                    threadIds.add(p.getThreadId());
                }
            }
            // also match thread subjects
            for (ThreadEntity t : threadRepository.findAll()) {
                if (t.getSubject() != null && t.getSubject().toLowerCase().contains(query.toLowerCase())) {
                    threadIds.add(t.getId());
                }
            }
            java.time.Instant cutoff = parseSince(since);
            int minReply = parseInt(minReplies, 0);
            for (UUID id : threadIds) {
                ThreadEntity t = threadRepository.findById(id).orElse(null);
                if (t == null) continue;
                Board b = boardRepository.findById(t.getBoardId()).orElse(null);
                if (board != null && !board.isBlank() && b != null && !board.equalsIgnoreCase(b.getSlug())) {
                    continue;
                }
                if (cutoff != null && t.getCreatedAt().isBefore(cutoff)) {
                    continue;
                }
                if (t.getReplyCount() < minReply) {
                    continue;
                }
                if (withImages != null && !withImages.isBlank() && t.getImageCount() == 0) {
                    continue;
                }
                results.add(Map.of(
                        "thread", t,
                        "boardSlug", b == null ? "general" : b.getSlug(),
                        "boardName", b == null ? "Board" : b.getName()));
            }
        }
        model.addAttribute("results", results);
        return "search";
    }

    private java.time.Instant parseSince(String since) {
        if (since == null || since.isBlank()) {
            return null;
        }
        try {
            return java.time.Instant.now().minus(java.time.Duration.ofDays(Long.parseLong(since)));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int parseInt(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}