package com.schoolchan.web;

<<<<<<< HEAD
import com.schoolchan.board.PostRepository;
import com.schoolchan.board.PostStatus;
=======
import com.schoolchan.board.Post;
import com.schoolchan.board.PostRepository;
import com.schoolchan.board.PostStatus;
import com.schoolchan.board.ThreadEntity;
import com.schoolchan.board.ThreadRepository;
>>>>>>> d5851d3 (Initial commit)
import com.schoolchan.reputation.LearningService;
import com.schoolchan.reputation.ReportRepository;
import com.schoolchan.reputation.ReportService;
import com.schoolchan.staff.AuditLog;
import com.schoolchan.staff.AuditLogRepository;
<<<<<<< HEAD
=======
import com.schoolchan.staff.StaffNote;
import java.util.List;
import java.util.UUID;
>>>>>>> d5851d3 (Initial commit)
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
<<<<<<< HEAD
=======
import org.springframework.web.bind.annotation.RequestParam;
>>>>>>> d5851d3 (Initial commit)
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final PostRepository postRepository;
<<<<<<< HEAD
    private final AuditLogRepository auditLogRepository;
    private final ReportService reportService;
    private final ReportRepository reportRepository;
=======
    private final ThreadRepository threadRepository;
    private final AuditLogRepository auditLogRepository;
    private final ReportService reportService;
    private final ReportRepository reportRepository;
    private final com.schoolchan.staff.StaffNoteRepository staffNoteRepository;
>>>>>>> d5851d3 (Initial commit)
    private final LearningService learningService;

    public StaffController(
            PostRepository postRepository,
<<<<<<< HEAD
            AuditLogRepository auditLogRepository,
            ReportService reportService,
            ReportRepository reportRepository,
            LearningService learningService) {
        this.postRepository = postRepository;
        this.auditLogRepository = auditLogRepository;
        this.reportService = reportService;
        this.reportRepository = reportRepository;
=======
            ThreadRepository threadRepository,
            AuditLogRepository auditLogRepository,
            ReportService reportService,
            ReportRepository reportRepository,
            com.schoolchan.staff.StaffNoteRepository staffNoteRepository,
            LearningService learningService) {
        this.postRepository = postRepository;
        this.threadRepository = threadRepository;
        this.auditLogRepository = auditLogRepository;
        this.reportService = reportService;
        this.reportRepository = reportRepository;
        this.staffNoteRepository = staffNoteRepository;
>>>>>>> d5851d3 (Initial commit)
        this.learningService = learningService;
    }

    @PostMapping("/posts/{postId}/publish")
    public String publish(@PathVariable UUID postId, @AuthenticationPrincipal UserDetails user) {
        postRepository.findById(postId).ifPresent(p -> {
            p.setStatus(PostStatus.PUBLISHED);
<<<<<<< HEAD
=======
            // A person looked at it and decided it stays. The flag is done.
            clearFlag(p);
>>>>>>> d5851d3 (Initial commit)
            postRepository.save(p);
            audit("PUBLISH", postId.toString(), user);
        });
        return "redirect:/staff/review";
    }

    @PostMapping("/posts/{postId}/block")
    public String block(@PathVariable UUID postId, @AuthenticationPrincipal UserDetails user) {
        postRepository.findById(postId).ifPresent(p -> {
            p.setStatus(PostStatus.BLOCKED);
<<<<<<< HEAD
=======
            clearFlag(p);
>>>>>>> d5851d3 (Initial commit)
            postRepository.save(p);
            reportRepository.findByPostId(postId).forEach(r -> reportService.confirm(r.getId()));
            learningService.learnFromConfirmation(p);
            audit("BLOCK_CONFIRM", postId.toString(), user);
        });
        return "redirect:/staff/review";
    }

<<<<<<< HEAD
=======
    /** A resolved post is no longer waiting for anyone. */
    private void clearFlag(Post post) {
        post.setFlagged(false);
        post.setFlagReason(null);
        post.setFlagSource(null);
    }

    @PostMapping("/review/bulk-publish")
    public String bulkPublish(@RequestParam(value = "postIds", required = false) List<UUID> postIds,
                              @AuthenticationPrincipal UserDetails user) {
        if (postIds != null) {
            for (UUID id : postIds) {
                postRepository.findById(id).ifPresent(p -> {
                    p.setStatus(PostStatus.PUBLISHED);
                    clearFlag(p);
                    postRepository.save(p);
                });
            }
            audit("BULK_PUBLISH", (postIds.size()) + " posts", user);
        }
        return "redirect:/staff/review";
    }

    @PostMapping("/review/bulk-block")
    public String bulkBlock(@RequestParam(value = "postIds", required = false) List<UUID> postIds,
                            @AuthenticationPrincipal UserDetails user) {
        if (postIds != null) {
            for (UUID id : postIds) {
                postRepository.findById(id).ifPresent(p -> {
                    p.setStatus(PostStatus.BLOCKED);
                    clearFlag(p);
                    postRepository.save(p);
                    reportRepository.findByPostId(id).forEach(r -> reportService.confirm(r.getId()));
                    learningService.learnFromConfirmation(p);
                });
            }
            audit("BULK_BLOCK", (postIds.size()) + " posts", user);
        }
        return "redirect:/staff/review";
    }

    @PostMapping("/notes")
    public String addNote(@RequestParam(required = false) UUID postId,
                          @RequestParam(required = false) UUID threadId,
                          @RequestParam String body,
                          @RequestParam(required = false) String redirect,
                          @AuthenticationPrincipal UserDetails user) {
        if (body == null || body.isBlank()) {
            return "redirect:/";
        }
        StaffNote note = new StaffNote();
        note.setId(UUID.randomUUID());
        note.setPostId(postId);
        note.setThreadId(threadId);
        note.setAuthor(user == null ? "staff" : user.getUsername());
        note.setBody(body.trim());
        staffNoteRepository.save(note);
        audit("STAFF_NOTE", (postId != null ? "post=" + postId : "thread=" + threadId), user);
        return "redirect:" + (redirect == null || redirect.isBlank() ? "/" : redirect);
    }

    @PostMapping("/posts/{postId}/flag")
    public String flagPost(@PathVariable UUID postId,
                           @RequestParam(required = false) String reason,
                           @AuthenticationPrincipal UserDetails user) {
        postRepository.findById(postId).ifPresent(p -> {
            p.setFlagged(true);
            p.setFlagSource("moderator");
            String note = "flagged by a moderator"
                    + (reason == null || reason.isBlank() ? "" : ": " + reason.trim());
            p.setFlagReason(p.getFlagReason() == null ? note : p.getFlagReason() + "; " + note);
            postRepository.save(p);
        });
        audit("POST_FLAG", postId.toString(), user);
        return "redirect:/staff/review";
    }

    @PostMapping("/reports/{reportId}/reject")
    public String rejectReport(@PathVariable UUID reportId, @AuthenticationPrincipal UserDetails user) {
        reportService.reject(reportId);
        audit("REPORT_REJECT", reportId.toString(), user);
        return "redirect:/staff/reports";
    }

    @PostMapping("/reports/{reportId}/confirm")
    public String confirmReport(@PathVariable UUID reportId, @AuthenticationPrincipal UserDetails user) {
        reportService.confirm(reportId);
        audit("REPORT_CONFIRM", reportId.toString(), user);
        return "redirect:/staff/reports";
    }

    @PostMapping("/threads/{threadId}/pin")
    public String pin(@PathVariable UUID threadId, @AuthenticationPrincipal UserDetails user) {
        threadRepository.findById(threadId).ifPresent(t -> {
            t.setPinned(!t.isPinned());
            threadRepository.save(t);
            audit("PIN_TOGGLE", threadId.toString(), user);
        });
        return "redirect:/staff/review";
    }

    @PostMapping("/threads/{threadId}/lock")
    public String lock(@PathVariable UUID threadId, @AuthenticationPrincipal UserDetails user) {
        threadRepository.findById(threadId).ifPresent(t -> {
            t.setLocked(!t.isLocked());
            threadRepository.save(t);
            audit("LOCK_TOGGLE", threadId.toString(), user);
        });
        return "redirect:/staff/review";
    }

>>>>>>> d5851d3 (Initial commit)
    private void audit(String action, String detail, UserDetails user) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setDetail(detail + (user != null ? " by " + user.getUsername() : ""));
        auditLogRepository.save(log);
    }
}
