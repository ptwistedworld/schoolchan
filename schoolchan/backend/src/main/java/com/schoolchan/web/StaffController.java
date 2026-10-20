package com.schoolchan.web;

import com.schoolchan.board.PostRepository;
import com.schoolchan.board.PostStatus;
import com.schoolchan.reputation.LearningService;
import com.schoolchan.reputation.ReportRepository;
import com.schoolchan.reputation.ReportService;
import com.schoolchan.staff.AuditLog;
import com.schoolchan.staff.AuditLogRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final PostRepository postRepository;
    private final AuditLogRepository auditLogRepository;
    private final ReportService reportService;
    private final ReportRepository reportRepository;
    private final LearningService learningService;

    public StaffController(
            PostRepository postRepository,
            AuditLogRepository auditLogRepository,
            ReportService reportService,
            ReportRepository reportRepository,
            LearningService learningService) {
        this.postRepository = postRepository;
        this.auditLogRepository = auditLogRepository;
        this.reportService = reportService;
        this.reportRepository = reportRepository;
        this.learningService = learningService;
    }

    @PostMapping("/posts/{postId}/publish")
    public String publish(@PathVariable UUID postId, @AuthenticationPrincipal UserDetails user) {
        postRepository.findById(postId).ifPresent(p -> {
            p.setStatus(PostStatus.PUBLISHED);
            postRepository.save(p);
            audit("PUBLISH", postId.toString(), user);
        });
        return "redirect:/staff/review";
    }

    @PostMapping("/posts/{postId}/block")
    public String block(@PathVariable UUID postId, @AuthenticationPrincipal UserDetails user) {
        postRepository.findById(postId).ifPresent(p -> {
            p.setStatus(PostStatus.BLOCKED);
            postRepository.save(p);
            reportRepository.findByPostId(postId).forEach(r -> reportService.confirm(r.getId()));
            learningService.learnFromConfirmation(p);
            audit("BLOCK_CONFIRM", postId.toString(), user);
        });
        return "redirect:/staff/review";
    }

    private void audit(String action, String detail, UserDetails user) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setDetail(detail + (user != null ? " by " + user.getUsername() : ""));
        auditLogRepository.save(log);
    }
}
