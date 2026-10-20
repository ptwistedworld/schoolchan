package com.schoolchan.staff;

import com.schoolchan.access.AccessService;
import com.schoolchan.access.AccessWindowRepository;
import com.schoolchan.board.BoardRepository;
import com.schoolchan.moderation.LearnedBlacklistCandidateRepository;
import com.schoolchan.moderation.ModerationResultRepository;
import com.schoolchan.reputation.LearningService;
import com.schoolchan.board.Post;
import com.schoolchan.board.PostRepository;
import com.schoolchan.board.PostStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/staff")
public class StaffDashboardController {

    private final PostRepository postRepository;
    private final EscalationRepository escalationRepository;
    private final BoardRepository boardRepository;
    private final AccessWindowRepository accessWindowRepository;
    private final RulesVersionRepository rulesVersionRepository;
    private final AccessService accessService;
    private final StaffSettingsService staffSettingsService;
    private final PosterMetadataRepository posterMetadataRepository;
    private final MetadataCryptoService metadataCryptoService;
    private final AuditLogRepository auditLogRepository;
    private final ModerationResultRepository moderationResultRepository;
    private final LearnedBlacklistCandidateRepository learnedCandidates;
    private final LearningService learningService;

    public StaffDashboardController(
            PostRepository postRepository,
            EscalationRepository escalationRepository,
            BoardRepository boardRepository,
            AccessWindowRepository accessWindowRepository,
            RulesVersionRepository rulesVersionRepository,
            AccessService accessService,
            StaffSettingsService staffSettingsService,
            PosterMetadataRepository posterMetadataRepository,
            MetadataCryptoService metadataCryptoService,
            AuditLogRepository auditLogRepository,
            ModerationResultRepository moderationResultRepository,
            LearnedBlacklistCandidateRepository learnedCandidates,
            LearningService learningService) {
        this.postRepository = postRepository;
        this.escalationRepository = escalationRepository;
        this.boardRepository = boardRepository;
        this.accessWindowRepository = accessWindowRepository;
        this.rulesVersionRepository = rulesVersionRepository;
        this.accessService = accessService;
        this.staffSettingsService = staffSettingsService;
        this.posterMetadataRepository = posterMetadataRepository;
        this.metadataCryptoService = metadataCryptoService;
        this.auditLogRepository = auditLogRepository;
        this.moderationResultRepository = moderationResultRepository;
        this.learnedCandidates = learnedCandidates;
        this.learningService = learningService;
    }

    @GetMapping("/login")
    public String login() {
        return "staff/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Post> held = postRepository.findAll().stream()
                .filter(p -> p.getStatus() == PostStatus.HELD_FOR_REVIEW)
                .toList();
        model.addAttribute("heldCount", held.size());
        model.addAttribute("escalationCount", escalationRepository.findByStatusOrderByCreatedAtDesc("NEW").size());
        return "staff/dashboard";
    }

    @GetMapping("/review")
    public String review(Model model) {
        List<Post> held = postRepository.findAll().stream()
                .filter(p -> p.getStatus() == PostStatus.HELD_FOR_REVIEW)
                .toList();
        java.util.Map<java.util.UUID, com.schoolchan.moderation.ModerationResult> results = new java.util.HashMap<>();
        for (Post p : held) {
            moderationResultRepository.findFirstByPostIdOrderByCreatedAtDesc(p.getId())
                    .ifPresent(r -> results.put(p.getId(), r));
        }
        model.addAttribute("posts", held);
        model.addAttribute("results", results);
        return "staff/review";
    }

    @GetMapping("/escalations")
    public String escalations(Model model) {
        model.addAttribute("escalations", escalationRepository.findAllByOrderByCreatedAtDesc());
        return "staff/escalations";
    }

    @GetMapping("/escalations/{id}")
    public String escalation(@PathVariable UUID id, Model model) {
        Escalation escalation = escalationRepository.findById(id).orElseThrow();
        model.addAttribute("escalation", escalation);
        postRepository.findById(escalation.getPostId()).ifPresent(p -> model.addAttribute("post", p));
        return "staff/escalation";
    }

    @PostMapping("/escalations/{id}/status")
    public String updateStatus(@PathVariable UUID id, @RequestParam String status, @RequestParam(required = false) String notes) {
        Escalation escalation = escalationRepository.findById(id).orElseThrow();
        escalation.setStatus(status);
        if (notes != null) {
            escalation.setNotes(notes);
        }
        escalation.setUpdatedAt(Instant.now());
        escalationRepository.save(escalation);
        audit("ESCALATION_STATUS", id + " -> " + status);
        return "redirect:/staff/escalations/" + id;
    }

    @PostMapping("/escalations/{id}/reveal")
    public String reveal(
            @PathVariable UUID id,
            @RequestParam String reason,
            RedirectAttributes redirectAttributes) {
        if (reason == null || reason.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "A reason is required before revealing metadata.");
            return "redirect:/staff/escalations/" + id;
        }
        Escalation escalation = escalationRepository.findById(id).orElseThrow();
        posterMetadataRepository.findByPostId(escalation.getPostId()).ifPresent(meta -> {
            redirectAttributes.addFlashAttribute("revealedIp", metadataCryptoService.decrypt(meta.getIpEncrypted()));
            redirectAttributes.addFlashAttribute(
                    "revealedSession", metadataCryptoService.decrypt(meta.getSessionHashEncrypted()));
        });
        audit("METADATA_REVEAL", "escalation=" + id + " reason=" + reason.trim());
        return "redirect:/staff/escalations/" + id;
    }

    @GetMapping("/escalations/{id}/package.txt")
    public ResponseEntity<String> packageText(@PathVariable UUID id) {
        Escalation escalation = escalationRepository.findById(id).orElseThrow();
        Post post = postRepository.findById(escalation.getPostId()).orElse(null);
        StringBuilder body = new StringBuilder();
        body.append("SchoolChan escalation package\n");
        body.append("id: ").append(escalation.getId()).append('\n');
        body.append("status: ").append(escalation.getStatus()).append('\n');
        body.append("category: ").append(escalation.getCategory()).append('\n');
        body.append("opened: ").append(escalation.getCreatedAt()).append('\n');
        if (post != null) {
            body.append("posted: ").append(post.getCreatedAt()).append('\n');
            body.append("original:\n").append(post.getOriginalText()).append('\n');
            if (post.getTranslatedText() != null && !post.getTranslatedText().isBlank()) {
                body.append("english:\n").append(post.getTranslatedText()).append('\n');
            }
        }
        if (escalation.getNotes() != null) {
            body.append("notes:\n").append(escalation.getNotes()).append('\n');
        }
        body.append("Poster IP and session are not included. Reveal them on the escalation page when needed.\n");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=escalation-" + id + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(body.toString());
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("boards", boardRepository.findAll());
        model.addAttribute("windows", accessWindowRepository.findAll());
        model.addAttribute("closedMode", accessService.closedMode());
        rulesVersionRepository.findTopByOrderByVersionDesc().ifPresent(r -> model.addAttribute("rules", r));
        model.addAttribute("candidates", learnedCandidates.findByApprovedFalseOrderByCreatedAtDesc());
        return "staff/settings";
    }

    @PostMapping("/settings/candidates/{id}/approve")
    public String approveCandidate(@PathVariable UUID id) {
        learningService.approveCandidate(id);
        audit("LEARNED_BLACKLIST", id.toString());
        return "redirect:/staff/settings";
    }

    @PostMapping("/settings/board")
    public String updateBoard(
            @RequestParam UUID boardId,
            @RequestParam int activeThreadCap,
            @RequestParam int bumpLimit,
            @RequestParam int imageLimit) {
        staffSettingsService.updateBoardLimits(boardId, activeThreadCap, bumpLimit, imageLimit);
        audit("BOARD_LIMITS", boardId.toString());
        return "redirect:/staff/settings";
    }

    @PostMapping("/settings/closed-mode")
    public String closedMode(@RequestParam String mode) {
        staffSettingsService.setClosedMode(mode);
        audit("CLOSED_MODE", mode);
        return "redirect:/staff/settings";
    }

    @PostMapping("/settings/rules")
    public String rules(@RequestParam String body) {
        staffSettingsService.saveRules(body);
        audit("RULES_UPDATE", "new version");
        return "redirect:/staff/settings";
    }

    @PostMapping("/settings/window")
    public String window(
            @RequestParam(required = false) UUID id,
            @RequestParam String label,
            @RequestParam String daysOfWeek,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(defaultValue = "false") boolean enabled) {
        staffSettingsService.saveWindow(id, label, daysOfWeek, startTime, endTime, enabled);
        audit("ACCESS_WINDOW", label);
        return "redirect:/staff/settings";
    }

    private void audit(String action, String detail) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setDetail(detail);
        auditLogRepository.save(log);
    }
}
