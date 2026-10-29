package com.schoolchan.staff;

import com.schoolchan.access.AccessService;
import com.schoolchan.access.AccessWindowRepository;
<<<<<<< HEAD
=======
import com.schoolchan.board.Board;
>>>>>>> d5851d3 (Initial commit)
import com.schoolchan.board.BoardRepository;
import com.schoolchan.moderation.LearnedBlacklistCandidateRepository;
import com.schoolchan.moderation.ModerationResultRepository;
import com.schoolchan.reputation.LearningService;
<<<<<<< HEAD
import com.schoolchan.board.Post;
import com.schoolchan.board.PostRepository;
import com.schoolchan.board.PostStatus;
=======
import com.schoolchan.reputation.ReportRepository;
import com.schoolchan.board.Post;
import com.schoolchan.board.PostRepository;
import com.schoolchan.board.PostStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.schoolchan.board.ThreadEntity;
import com.schoolchan.board.ThreadRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
import java.util.List;
=======
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
>>>>>>> d5851d3 (Initial commit)
import java.util.UUID;

@Controller
@RequestMapping("/staff")
public class StaffDashboardController {

    private final PostRepository postRepository;
<<<<<<< HEAD
=======
    private final ThreadRepository threadRepository;
    private final StaffUserRepository staffUserRepository;
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD

    public StaffDashboardController(
            PostRepository postRepository,
=======
    private final PasswordEncoder passwordEncoder;
    private final ReportRepository reportRepository;
    private final com.schoolchan.reputation.ReputationEntryRepository reputationRepository;
    private final com.schoolchan.board.SiteSettingRepository siteSettingRepository;
    private final com.schoolchan.eval.TrainingExampleRepository trainingExampleRepository;
    private final com.schoolchan.config.SchoolChanProperties properties;
    private final com.schoolchan.student.StudentService studentService;
    private final com.schoolchan.student.StudentAccessPolicy studentAccessPolicy;
    private final com.schoolchan.board.AppealService appealService;
    private final com.schoolchan.board.BoardRuleRepository boardRuleRepository;
    private final com.schoolchan.board.PosterTierService posterTierService;
    private final ModerationSightingRepository sightingRepository;
    private final com.schoolchan.moderation.ModerationPolicy moderationPolicy;
    private final com.schoolchan.media.MediaRepository mediaRepository;
    private final com.schoolchan.media.MediaMaintenanceJob mediaMaintenanceJob;

    public StaffDashboardController(
            PostRepository postRepository,
            ThreadRepository threadRepository,
            StaffUserRepository staffUserRepository,
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
            LearningService learningService) {
        this.postRepository = postRepository;
=======
            LearningService learningService,
            PasswordEncoder passwordEncoder,
            ReportRepository reportRepository,
            com.schoolchan.reputation.ReputationEntryRepository reputationRepository,
            com.schoolchan.board.SiteSettingRepository siteSettingRepository,
            com.schoolchan.eval.TrainingExampleRepository trainingExampleRepository,
            com.schoolchan.config.SchoolChanProperties properties,
            com.schoolchan.student.StudentService studentService,
            com.schoolchan.student.StudentAccessPolicy studentAccessPolicy,
            com.schoolchan.board.AppealService appealService,
            com.schoolchan.board.BoardRuleRepository boardRuleRepository,
            com.schoolchan.board.PosterTierService posterTierService,
            ModerationSightingRepository sightingRepository,
            com.schoolchan.moderation.ModerationPolicy moderationPolicy,
            com.schoolchan.media.MediaRepository mediaRepository,
            com.schoolchan.media.MediaMaintenanceJob mediaMaintenanceJob) {
        this.postRepository = postRepository;
        this.threadRepository = threadRepository;
        this.staffUserRepository = staffUserRepository;
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
=======
        this.passwordEncoder = passwordEncoder;
        this.reportRepository = reportRepository;
        this.reputationRepository = reputationRepository;
        this.siteSettingRepository = siteSettingRepository;
        this.trainingExampleRepository = trainingExampleRepository;
        this.properties = properties;
        this.studentService = studentService;
        this.studentAccessPolicy = studentAccessPolicy;
        this.appealService = appealService;
        this.boardRuleRepository = boardRuleRepository;
        this.posterTierService = posterTierService;
        this.sightingRepository = sightingRepository;
        this.moderationPolicy = moderationPolicy;
        this.mediaRepository = mediaRepository;
        this.mediaMaintenanceJob = mediaMaintenanceJob;
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
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

=======
        // Flagged posts are published but still waiting for a person, so they
        // are part of the review workload.
        model.addAttribute("flaggedCount", postRepository.findAll().stream()
                .filter(com.schoolchan.board.Post::isFlagged).count());
        model.addAttribute("escalationCount", escalationRepository.findByStatusOrderByCreatedAtDesc("NEW").size());
        model.addAttribute("reportCount", reportRepository.count());
        model.addAttribute("publishedCount", postRepository.findAll().stream()
                .filter(p -> p.getStatus() == PostStatus.PUBLISHED).count());
        model.addAttribute("blockedCount", postRepository.findAll().stream()
                .filter(p -> p.getStatus() == PostStatus.BLOCKED).count());
        model.addAttribute("latency", latencyStats());
        model.addAttribute("decisions", decisionCounts());
        model.addAttribute("reputation", reputationStats());
        model.addAttribute("languages", languageBreakdown());
        model.addAttribute("confidence", languageConfidence());
        model.addAttribute("tiers", posterTierService.all());
        // If the moderation service cannot be reached, every new post waits for
        // a person. Say so, so nobody thinks the board is full of bad posts.
        long degraded = moderationResultRepository.findAll().stream()
                .filter(com.schoolchan.moderation.ModerationResult::isMlUnavailable)
                .count();
        model.addAttribute("degradedCount", degraded);
        model.addAttribute("sidecarReachable", sidecarReachable());
        return "staff/dashboard";
    }

    /** Per-stage latency statistics — evidence for the ISO 25010 write-up. */
    private List<Map<String, Object>> latencyStats() {
        Map<String, List<Long>> byStage = new java.util.LinkedHashMap<>();
        List<Long> totals = new java.util.ArrayList<>();
        for (com.schoolchan.moderation.ModerationResult r : moderationResultRepository.findAll()) {
            totals.add(r.getTotalLatencyMs());
            try {
                var node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(r.getStageResultsJson());
                if (node.isArray()) {
                    for (var item : node) {
                        String stage = item.path("stage").asText("unknown");
                        byStage.computeIfAbsent(stage, k -> new java.util.ArrayList<>())
                                .add(item.path("latencyMs").asLong(0));
                    }
                }
            } catch (Exception ignored) {
            }
        }
        List<Map<String, Object>> rows = new java.util.ArrayList<>();
        byStage.forEach((stage, values) -> {
            Map<String, Object> row = new java.util.LinkedHashMap<>();
            row.put("stage", stage);
            row.put("count", values.size());
            row.put("mean", Math.round(avg(values)));
            row.put("max", values.stream().mapToLong(Long::longValue).max().orElse(0));
            row.put("p95", percentile(values, 0.95));
            rows.add(row);
        });
        rows.sort((a, b) -> Long.compare((long) b.get("max"), (long) a.get("max")));
        return rows;
    }

    private long totalLatencyMedian() {
        return 0;
    }

    private double avg(List<Long> values) {
        if (values.isEmpty()) {
            return 0;
        }
        return values.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    private long percentile(List<Long> values, double p) {
        if (values.isEmpty()) {
            return 0;
        }
        List<Long> sorted = new java.util.ArrayList<>(values);
        sorted.sort(Long::compareTo);
        int idx = (int) Math.ceil(p * sorted.size()) - 1;
        return sorted.get(Math.max(0, Math.min(sorted.size() - 1, idx)));
    }

    /** Counts of each final moderation decision — the accuracy evidence table. */
    private List<Map<String, Object>> decisionCounts() {
        Map<String, Long> counts = new java.util.LinkedHashMap<>();
        for (com.schoolchan.moderation.ModerationResult r : moderationResultRepository.findAll()) {
            counts.merge(r.getDecision().name(), 1L, Long::sum);
        }
        Map<String, String> labels = Map.of(
                "PUBLISH", "published",
                "QUEUE_FOR_REVIEW", "published, flagged for review",
                "BLOCK", "blocked by the filter",
                "ESCALATE", "escalated for safety");
        List<Map<String, Object>> rows = new java.util.ArrayList<>();
        counts.forEach((k, v) -> {
            Map<String, Object> row = new java.util.LinkedHashMap<>();
            row.put("decision", labels.getOrDefault(k, k));
            row.put("count", v);
            rows.add(row);
        });
        return rows;
    }

    /**
     * Per-language breakdown of screened posts. Feeds the paper's
     * "accuracy per language" table (English / Tagalog / Cebuano).
     */
    private List<Map<String, Object>> languageBreakdown() {
        // Counted from the moderation decision, not the post status: under the
        // current policy every post is published, so the status would say
        // nothing. The decision still shows what the pipeline thought.
        Map<java.util.UUID, com.schoolchan.moderation.ModerationDecision> decisions =
                new java.util.LinkedHashMap<>();
        for (var r : moderationResultRepository.findAll()) {
            decisions.put(r.getPostId(), r.getDecision());
        }
        Map<String, long[]> counts = new java.util.LinkedHashMap<>();
        for (Post p : postRepository.findAll()) {            String lang = p.getDetectedLanguage();
            if (lang == null || lang.isBlank()) {
                lang = "unknown";
            }
            long[] c = counts.computeIfAbsent(lang, k -> new long[2]);
            c[0]++;
            if (decisions.get(p.getId()) == com.schoolchan.moderation.ModerationDecision.PUBLISH) {
                c[1]++;
            }
        }
        List<Map<String, Object>> rows = new java.util.ArrayList<>();
        counts.forEach((lang, c) -> {
            Map<String, Object> row = new java.util.LinkedHashMap<>();
            row.put("language", lang);
            row.put("total", c[0]);
            row.put("published", c[1]);
            row.put("flagged", c[0] - c[1]);
            row.put("publishRate", c[0] == 0 ? 0 : Math.round((c[1] * 1000.0) / c[0]) / 10.0);
            rows.add(row);
        });
        rows.sort((a, b) -> Long.compare((long) b.get("total"), (long) a.get("total")));
        return rows;
    }

    /**
     * Moderation confidence per language. Shows where the model is weak
     * (Cebuano, usually) so the decision is honest about its own limits.
     */
    private List<Map<String, Object>> languageConfidence() {
        java.util.Map<String, double[]> acc = new java.util.LinkedHashMap<>();
        List<com.schoolchan.moderation.ModerationResult> results = moderationResultRepository.findAll();
        // One query for the posts, not one per result. This used to be the
        // reason the staff dashboard ran hundreds of queries.
        java.util.Map<java.util.UUID, String> languageByPost = new java.util.HashMap<>();
        if (!results.isEmpty()) {
            java.util.Set<java.util.UUID> ids = new java.util.HashSet<>();
            for (var r : results) {
                ids.add(r.getPostId());
            }
            for (Post p : postRepository.findByIdIn(ids)) {
                languageByPost.put(p.getId(), p.getDetectedLanguage());
            }
        }
        for (var r : results) {
            String detected = languageByPost.get(r.getPostId());
            String lang = (detected == null || detected.isBlank()) ? "unknown" : detected;
            double[] stats = acc.computeIfAbsent(lang, k -> new double[3]);
            stats[0] += 1;
            if (r.getDecision() == com.schoolchan.moderation.ModerationDecision.PUBLISH) {
                stats[1] += 1;
            }
            if (r.isMlUnavailable()) {
                stats[2] += 1;
            }
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        acc.forEach((lang, s) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("language", lang);
            row.put("screened", (long) s[0]);
            row.put("published", (long) s[1]);
            row.put("held", (long) (s[0] - s[1]));
            row.put("flagged", (long) (s[0] - s[1]));
            row.put("degraded", (long) s[2]);
            row.put("publishRate", s[0] == 0 ? 0 : Math.round((s[1] * 1000.0) / s[0]) / 10.0);
            rows.add(row);
        });
        rows.sort((a, b) -> Long.compare((long) b.get("screened"), (long) a.get("screened")));
        return rows;
    }

    /** Is the moderation sidecar answering? */
    private boolean sidecarReachable() {
        try {
            var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(1200);
            factory.setReadTimeout(2000);
            var rest = new org.springframework.web.client.RestTemplate(factory);
            var body = rest.getForObject(properties.getMl().getBaseUrl() + "/health", String.class);
            return body != null;
        } catch (Exception e) {  // noqa: BLE001
            return false;
        }
    }

    /** Anonymised reputation distribution — no session identifiers are shown. */
    private Map<String, Object> reputationStats() {
        List<com.schoolchan.reputation.ReputationEntry> entries = reputationRepository.findAll();
        double sum = 0;
        double min = 2.0;
        double max = 0.0;
        int strikes = 0;
        for (var e : entries) {
            sum += e.getReliability();
            min = Math.min(min, e.getReliability());
            max = Math.max(max, e.getReliability());
            if (e.getStrikeLevel() > 0) {
                strikes++;
            }
        }
        Map<String, Object> out = new java.util.LinkedHashMap<>();
        out.put("sessions", entries.size());
        out.put("mean", entries.isEmpty() ? 0 : Math.round((sum / entries.size()) * 100) / 100.0);
        out.put("min", entries.isEmpty() ? 0 : Math.round(min * 100) / 100.0);
        out.put("max", Math.round(max * 100) / 100.0);
        out.put("strikes", strikes);
        return out;
    }

    @GetMapping("/review")
    public String review(Model model,
                         @RequestParam(required = false) String filter,
                         @RequestParam(required = false) String reason,
                         @RequestParam(defaultValue = "queue") String view) {
        List<Post> held = postRepository.findByStatus(PostStatus.HELD_FOR_REVIEW);
        // Community reports are also moderation work, even when the post is
        // still published. Surface them in the same queue.
        List<com.schoolchan.reputation.Report> openReports =
                reportRepository.findByStatusOrderByCreatedAtDesc("OPEN");
        java.util.Map<java.util.UUID, List<com.schoolchan.reputation.Report>> reportsByPost =
                new java.util.HashMap<>();
        for (com.schoolchan.reputation.Report r : openReports) {
            reportsByPost.computeIfAbsent(r.getPostId(), k -> new java.util.ArrayList<>()).add(r);
        }
        java.util.List<Post> posts = new java.util.ArrayList<>(held);
        java.util.Set<java.util.UUID> seen = new java.util.HashSet<>();
        for (Post p : held) {
            seen.add(p.getId());
        }
        // Posts the filter flagged, a student reported, or a moderator flagged.
        // A post staff already removed, or that the author deleted, is settled
        // and does not belong in the queue.
        for (Post p : postRepository.findByFlaggedTrue()) {
            if (p.getStatus() == PostStatus.BLOCKED || p.isDeletedByAuthor()) {
                continue;
            }
            if (seen.add(p.getId())) {
                posts.add(p);
            }
        }
        for (var entry : reportsByPost.entrySet()) {
            Post p = postRepository.findById(entry.getKey()).orElse(null);
            if (p != null && p.getStatus() != PostStatus.BLOCKED && !p.isDeletedByAuthor()
                    && seen.add(p.getId())) {
                posts.add(p);
            }
        }
        posts.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));

        // "All decisions" shows every post the pipeline screened, including the
        // ones it let through with a flag. Without this, a flagged post that
        // was published looks like nothing happened.
        if ("all".equals(view)) {
            // The full log, newest first. Bounded, so a busy board does not
            // pull every post in the database into memory.
            List<Post> every = postRepository.findTop200ByOrderByCreatedAtDesc();
            posts = every;
        }

        // Optional filters: "all" (default), "held" only, "flagged" only, or
        // "reported" only.
        String activeFilter = filter == null || filter.isBlank() ? "all" : filter;
        if ("held".equals(activeFilter)) {
            posts = posts.stream().filter(p -> p.getStatus() == PostStatus.HELD_FOR_REVIEW).toList();
        } else if ("flagged".equals(activeFilter)) {
            posts = posts.stream().filter(Post::isFlagged).toList();
        } else if ("reported".equals(activeFilter)) {
            posts = posts.stream().filter(p -> reportsByPost.containsKey(p.getId())).toList();
        }
        if (reason != null && !reason.isBlank()) {
            posts = posts.stream().filter(p -> {
                var rs = reportsByPost.get(p.getId());
                return rs != null && rs.stream().anyMatch(r -> reason.equalsIgnoreCase(r.getReason()));
            }).toList();
        }

        java.util.Map<java.util.UUID, com.schoolchan.moderation.ModerationResult> results = new java.util.HashMap<>();
        java.util.Map<java.util.UUID, List<Map<String, Object>>> stageDetails = new java.util.HashMap<>();
        // A moderator cannot judge what they cannot read, so the queue carries
        // the full content of every post: the text as written, the English
        // translation when there is one, the thread it came from, and anything
        // the student attached.
        java.util.Map<java.util.UUID, Map<String, Object>> contextByPost = new java.util.HashMap<>();
        for (Post p : posts) {
            Map<String, Object> context = new LinkedHashMap<>();
            var thread = threadRepository.findById(p.getThreadId()).orElse(null);
            context.put("threadId", p.getThreadId());
            context.put("subject", thread == null ? "(thread gone)" : thread.getSubject());
            context.put("board", thread == null ? ""
                    : boardRepository.findById(thread.getBoardId()).map(b -> b.getName()).orElse(""));
            context.put("boardSlug", thread == null ? ""
                    : boardRepository.findById(thread.getBoardId()).map(b -> b.getSlug()).orElse(""));
            context.put("postNumber", p.getBoardPostNumber());
            context.put("media", mediaRepository.findByPostId(p.getId()));
            contextByPost.put(p.getId(), context);
        }
        for (Post p : posts) {
            moderationResultRepository.findFirstByPostIdOrderByCreatedAtDesc(p.getId())
                    .ifPresent(r -> {
                        results.put(p.getId(), r);
                        stageDetails.put(p.getId(), parseStages(r.getStageResultsJson()));
                    });
        }
        model.addAttribute("posts", posts);
        model.addAttribute("results", results);
        model.addAttribute("stageDetails", stageDetails);
        model.addAttribute("contextByPost", contextByPost);
        model.addAttribute("reportsByPost", reportsByPost);
        model.addAttribute("reportCount", openReports.size());
        model.addAttribute("filter", activeFilter);
        model.addAttribute("reason", reason == null ? "" : reason);
        model.addAttribute("view", view);
        // Age indicator: how long each item has been waiting.
        java.util.Map<java.util.UUID, Map<String, Object>> ages = new java.util.HashMap<>();
        Instant now = Instant.now();
        for (Post p : posts) {
            long minutes = java.time.Duration.between(p.getCreatedAt(), now).toMinutes();
            Map<String, Object> age = new java.util.LinkedHashMap<>();
            age.put("label", (minutes / 60) + "h " + (minutes % 60) + "m");
            age.put("class", minutes >= 24 * 60 ? "age-old" : (minutes >= 60 ? "age-mid" : "age-new"));
            ages.put(p.getId(), age);
        }
        model.addAttribute("ages", ages);
        model.addAttribute("flagSources", java.util.Map.of(
                "filter", "the automatic filter",
                "student", "reported by a student",
                "moderator", "flagged by a moderator",
                "reputation", "the poster's trust score",
                "escalation", "a safety concern"));
        model.addAttribute("decisionLabels", java.util.Map.of(
                com.schoolchan.moderation.ModerationDecision.PUBLISH, "published",
                com.schoolchan.moderation.ModerationDecision.QUEUE_FOR_REVIEW, "published, flagged for review",
                com.schoolchan.moderation.ModerationDecision.BLOCK, "blocked by the filter",
                com.schoolchan.moderation.ModerationDecision.ESCALATE, "escalated for safety"));
        return "staff/review";
    }

    /** Turns the stored stage-results JSON into a list staff can read. */
    private List<Map<String, Object>> parseStages(String json) {
        List<Map<String, Object>> out = new java.util.ArrayList<>();
        if (json == null || json.isBlank()) {
            return out;
        }
        try {
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var node = mapper.readTree(json);
            if (node.isArray()) {
                for (var item : node) {
                    Map<String, Object> row = new java.util.LinkedHashMap<>();
                    row.put("stage", item.path("stage").asText(""));
                    row.put("score", item.path("score").asDouble(0));
                    row.put("latencyMs", item.path("latencyMs").asLong(0));
                    List<String> reasons = new java.util.ArrayList<>();
                    if (item.path("reasons").isArray()) {
                        item.path("reasons").forEach(r -> reasons.add(r.asText()));
                    }
                    row.put("reasons", String.join(", ", reasons));
                    row.put("mlUnavailable", item.path("mlUnavailable").asBoolean(false));
                    out.add(row);
                }
            }
        } catch (Exception ignored) {
        }
        return out;
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        List<com.schoolchan.reputation.Report> open = reportRepository.findByStatusOrderByCreatedAtDesc("OPEN");
        java.util.Map<java.util.UUID, Post> posts = new java.util.HashMap<>();
        for (com.schoolchan.reputation.Report r : open) {
            postRepository.findById(r.getPostId()).ifPresent(p -> posts.put(r.getPostId(), p));
        }
        model.addAttribute("reports", open);
        model.addAttribute("posts", posts);
        model.addAttribute("pageTitle", "Reports — SchoolChan staff");
        return "staff/reports";
    }

    @GetMapping("/escalations")
    public String escalations(Model model) {
        model.addAttribute("escalations", escalationRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("rehearsal", staffSettingsService.rehearsalMode());
        return "staff/escalations";
    }

    @GetMapping("/threads")
    public String threads(Model model) {
        java.util.Map<java.util.UUID, com.schoolchan.board.Board> boards = new java.util.HashMap<>();
        for (com.schoolchan.board.Board b : boardRepository.findAll()) {
            boards.put(b.getId(), b);
        }
        List<Map<String, Object>> rows = new java.util.ArrayList<>();
        for (com.schoolchan.board.ThreadEntity t : threadRepository.findAll()) {
            com.schoolchan.board.Board b = boards.get(t.getBoardId());
            Map<String, Object> row = new java.util.LinkedHashMap<>();
            row.put("thread", t);
            row.put("boardSlug", b == null ? "general" : b.getSlug());
            row.put("boardName", b == null ? "—" : b.getName());
            rows.add(row);
        }
        model.addAttribute("rows", rows);
        model.addAttribute("boards", boardRepository.findAll());
        return "staff/threads";
    }

    @GetMapping("/staff-users")
    public String staffUsers(Model model) {
        model.addAttribute("staffUsers", staffUserRepository.findAll());
        return "staff/staff-users";
    }

    @PostMapping("/staff-users/create")
    public String createStaffUser(@RequestParam String username, @RequestParam String password, @RequestParam String role) {
        StaffUser u = new StaffUser();
        u.setId(UUID.randomUUID());
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setRole(role);
        u.setEnabled(true);
        staffUserRepository.save(u);
        audit("STAFF_CREATE", username);
        return "redirect:/staff/staff-users";
    }

    @PostMapping("/staff-users/{id}/toggle")
    public String toggleStaffUser(@PathVariable UUID id) {
        staffUserRepository.findById(id).ifPresent(u -> {
            u.setEnabled(!u.isEnabled());
            staffUserRepository.save(u);
            audit("STAFF_TOGGLE", u.getUsername());
        });
        return "redirect:/staff/staff-users";
    }

    @GetMapping("/audit")
    public String audit(Model model) {
        model.addAttribute("logs", auditLogRepository.findAllByOrderByCreatedAtDesc());
        return "staff/audit";
    }

    @GetMapping("/audit.csv")
    public ResponseEntity<String> auditCsv() {
        StringBuilder sb = new StringBuilder("id,created_at,action,detail\n");
        for (AuditLog log : auditLogRepository.findAllByOrderByCreatedAtDesc()) {
            sb.append(log.getId()).append(',')
              .append('"').append(log.getCreatedAt()).append('"').append(',')
              .append('"').append(csv(log.getAction())).append('"').append(',')
              .append('"').append(csv(log.getDetail())).append('"').append('\n');
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=schoolchan-audit.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(sb.toString());
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\"\"").replace("\n", " ");
    }

    @GetMapping("/escalations/{id}")
    public String escalation(@PathVariable UUID id, Model model) {
        Escalation escalation = escalationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No such escalation case."));
        boolean rehearsal = staffSettingsService.rehearsalMode();
        model.addAttribute("escalation", escalation);
        model.addAttribute("rehearsal", rehearsal);
        model.addAttribute("sightings", sightingRepository.findByEscalationIdOrderByCreatedAtDesc(id));
        model.addAttribute("nextSteps", nextSteps(escalation.getStatus()));
        if (!rehearsal) {
            postRepository.findById(escalation.getPostId()).ifPresent(p -> model.addAttribute("post", p));
        }
        return "staff/escalation";
    }

    /** What should happen next, so nobody has to guess. */
    private List<String> nextSteps(String status) {
        String s = status == null ? "NEW" : status;
        return switch (s) {
            case "NEW" -> List.of(
                    "Read the post and decide whether this is a genuine safety risk.",
                    "If it is, mark it UNDER_REVIEW and note what you did.",
                    "If it is not a real risk, close it as a false positive.");
            case "UNDER_REVIEW" -> List.of(
                    "Check whether the post names a specific, immediate danger.",
                    "If it does, forward it to the guidance office and set FORWARDED.",
                    "Write down what was passed on, without the poster's details.");
            case "FORWARDED" -> List.of(
                    "The school has the package. Follow up if they need anything.",
                    "Close as CLOSED_ACTIONED once the school confirms, or CLOSED_FALSE_POSITIVE if it was not acted on.");
            case "CLOSED_ACTIONED" -> List.of("No further action. The record stays in the audit log.");
            case "CLOSED_FALSE_POSITIVE" -> List.of("No further action. Consider adding a keyword so it is caught next time.");
            default -> List.of("Mark it UNDER_REVIEW to begin.");
        };
    }

    /** "I saw this" — a one-click note that a moderator has looked at the case. */
    @PostMapping("/escalations/{id}/seen")
    public String markSeen(@PathVariable UUID id,
                           @RequestParam(required = false) String note,
                           @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails user) {
        ModerationSighting sighting = new ModerationSighting();
        sighting.setId(UUID.randomUUID());
        sighting.setEscalationId(id);
        sighting.setStaffName(user == null ? "staff" : user.getUsername());
        sighting.setNote(note);
        sightingRepository.save(sighting);
        audit("ESCALATION_SEEN", id.toString());
        return "redirect:/staff/escalations/" + id;
    }

>>>>>>> d5851d3 (Initial commit)
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

<<<<<<< HEAD
    @GetMapping("/escalations/{id}/package.txt")
    public ResponseEntity<String> packageText(@PathVariable UUID id) {
        Escalation escalation = escalationRepository.findById(id).orElseThrow();
        Post post = postRepository.findById(escalation.getPostId()).orElse(null);
        StringBuilder body = new StringBuilder();
        body.append("SchoolChan escalation package\n");
=======
    @GetMapping("/escalations/{id}/print")
    public String printPackage(@PathVariable UUID id, Model model) {
        Escalation escalation = escalationRepository.findById(id).orElseThrow();
        boolean rehearsal = staffSettingsService.rehearsalMode();
        model.addAttribute("escalation", escalation);
        model.addAttribute("rehearsal", rehearsal);
        if (!rehearsal) {
            postRepository.findById(escalation.getPostId()).ifPresent(p -> model.addAttribute("post", p));
        }
        return "staff/escalation-print";
    }

    @GetMapping("/escalations/{id}/package.txt")
    public ResponseEntity<String> packageText(@PathVariable UUID id) {
        Escalation escalation = escalationRepository.findById(id).orElseThrow();
        boolean rehearsal = staffSettingsService.rehearsalMode();
        Post post = rehearsal ? null : postRepository.findById(escalation.getPostId()).orElse(null);
        StringBuilder body = new StringBuilder();
        body.append("SchoolChan escalation package\n");
        if (rehearsal) {
            body.append("REHEARSAL RUN - NO REAL POST DATA IN THIS PACKAGE\n");
        }
>>>>>>> d5851d3 (Initial commit)
        body.append("id: ").append(escalation.getId()).append('\n');
        body.append("status: ").append(escalation.getStatus()).append('\n');
        body.append("category: ").append(escalation.getCategory()).append('\n');
        body.append("opened: ").append(escalation.getCreatedAt()).append('\n');
<<<<<<< HEAD
=======
        if (rehearsal) {
            body.append("original:\n[SYNTHETIC PLACEHOLDER - rehearsal mode]\n");
        }
>>>>>>> d5851d3 (Initial commit)
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

<<<<<<< HEAD
    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("boards", boardRepository.findAll());
        model.addAttribute("windows", accessWindowRepository.findAll());
        model.addAttribute("closedMode", accessService.closedMode());
=======
    @GetMapping("/appeals")
    public String appeals(Model model) {
        model.addAttribute("appeals", appealService.pending());
        model.addAttribute("pageTitle", "Appeals — SchoolChan staff");
        return "staff/appeals";
    }

    @PostMapping("/appeals/{id}")
    public String resolveAppeal(@PathVariable UUID id,
                                @RequestParam(defaultValue = "false") boolean approved,
                                @RequestParam(required = false) String postId) {
        appealService.resolve(id, approved);
        if (approved && postId != null && !postId.isBlank()) {
            try {
                postRepository.findById(UUID.fromString(postId)).ifPresent(p -> {
                    p.setStatus(PostStatus.PUBLISHED);
                    postRepository.save(p);
                });
            } catch (IllegalArgumentException ignored) {
                // not a post id
            }
        }
        audit("APPEAL_RESOLVE", id + " approved=" + approved);
        return "redirect:/staff/appeals";
    }

    /** Who did what, grouped by moderator: the "moderators are reviewable" view. */
    @GetMapping("/accountability")
    public String accountability(Model model) {
        java.util.Map<String, java.util.Map<String, Long>> byStaff = new java.util.LinkedHashMap<>();
        for (AuditLog log : auditLogRepository.findAllByOrderByCreatedAtDesc()) {
            String action = log.getAction() == null ? "UNKNOWN" : log.getAction();
            String who = "unknown";
            if (log.getDetail() != null) {
                int idx = log.getDetail().lastIndexOf(" by ");
                if (idx > 0) {
                    who = log.getDetail().substring(idx + 4);
                }
            }
            byStaff.computeIfAbsent(who, k -> new java.util.LinkedHashMap<>())
                    .merge(action, 1L, Long::sum);
        }
        model.addAttribute("byStaff", byStaff);
        model.addAttribute("pageTitle", "Moderator accountability — SchoolChan staff");
        return "staff/accountability";
    }

    /** Moderation rules that apply to one board only. */
    @GetMapping("/board-rules")
    public String boardRules(@RequestParam(required = false) String board, Model model) {
        var boards = boardRepository.findAll();
        // Open on the first board instead of an empty "choose a board" page.
        String selected = board;
        if ((selected == null || selected.isBlank()) && !boards.isEmpty()) {
            selected = boards.get(0).getSlug();
        }
        model.addAttribute("boards", boards);
        model.addAttribute("selected", selected);
        if (selected != null && !selected.isBlank()) {
            String slug = selected;
            boardRepository.findBySlug(slug).ifPresent(b -> {
                model.addAttribute("selectedBoard", b);
                model.addAttribute("rules", boardRuleRepository.findByBoardIdOrderByPatternAsc(b.getId()));
            });
        }
        model.addAttribute("pageTitle", "Board rules — SchoolChan staff");
        return "staff/board-rules";
    }

    @PostMapping("/board-rules/add")
    public String addBoardRule(@RequestParam String board,
                               @RequestParam String pattern,
                               @RequestParam(defaultValue = "WORD") String matchType,
                               @RequestParam(defaultValue = "OTHER") String category,
                               @RequestParam(defaultValue = "0.6") double severity) {
        boardRepository.findBySlug(board).ifPresent(b -> {
            com.schoolchan.board.BoardRule rule = new com.schoolchan.board.BoardRule();
            rule.setId(UUID.randomUUID());
            rule.setBoardId(b.getId());
            rule.setPattern(pattern.trim());
            rule.setMatchType(matchType.toUpperCase());
            rule.setCategory(category.toUpperCase());
            rule.setSeverity(Math.max(0, Math.min(1, severity)));
            rule.setEnabled(true);
            boardRuleRepository.save(rule);
        });
        audit("BOARD_RULE_ADD", board + ":" + pattern);
        return "redirect:/staff/board-rules?board=" + board;
    }

    @PostMapping("/board-rules/{id}/toggle")
    public String toggleBoardRule(@PathVariable UUID id) {
        String slug = boardRuleRepository.findById(id).map(r -> {
            r.setEnabled(!r.isEnabled());
            boardRuleRepository.save(r);
            return boardRepository.findById(r.getBoardId()).map(b -> b.getSlug()).orElse("");
        }).orElse("");
        return "redirect:/staff/board-rules" + (slug.isBlank() ? "" : "?board=" + slug);
    }

    @PostMapping("/board-rules/{id}/delete")
    public String deleteBoardRule(@PathVariable UUID id) {
        String slug = boardRuleRepository.findById(id).map(r -> {
            String s = boardRepository.findById(r.getBoardId()).map(b -> b.getSlug()).orElse("");
            boardRuleRepository.delete(r);
            return s;
        }).orElse("");
        return "redirect:/staff/board-rules" + (slug.isBlank() ? "" : "?board=" + slug);
    }

    /** A weekly summary of what happened on the board. */
    @GetMapping("/digest")
    public String digest(Model model) {
        java.time.Instant weekAgo = java.time.Instant.now().minus(7, java.time.temporal.ChronoUnit.DAYS);
        List<Post> week = postRepository.findAll().stream()
                .filter(p -> p.getCreatedAt().isAfter(weekAgo))
                .toList();
        long published = week.stream().filter(p -> p.getStatus() == PostStatus.PUBLISHED).count();
        long flagged = week.stream().filter(Post::isFlagged).count();
        long held = week.stream().filter(p -> p.getStatus() == PostStatus.HELD_FOR_REVIEW).count();
        long blocked = week.stream().filter(p -> p.getStatus() == PostStatus.BLOCKED).count();
        long posts = postRepository.count();
        long threads = threadRepository.count();
        long reports = reportRepository.count();
        model.addAttribute("published", published);
        model.addAttribute("flagged", flagged);
        model.addAttribute("held", held);
        model.addAttribute("blocked", blocked);
        model.addAttribute("totalPosts", posts);
        model.addAttribute("totalThreads", threads);
        model.addAttribute("totalReports", reports);
        model.addAttribute("languages", languageBreakdown());
        model.addAttribute("topBoards", topBoards());
        model.addAttribute("pageTitle", "Weekly digest — SchoolChan staff");
        return "staff/digest";
    }

    private List<Map<String, Object>> topBoards() {
        java.util.Map<String, Long> counts = new java.util.LinkedHashMap<>();
        for (Board b : boardRepository.findAll()) {
            counts.put(b.getName(), threadRepository.countActiveByBoard(b.getId()));
        }
        List<Map<String, Object>> rows = new java.util.ArrayList<>();
        counts.forEach((name, n) -> {
            Map<String, Object> row = new java.util.LinkedHashMap<>();
            row.put("name", name);
            row.put("threads", n);
            rows.add(row);
        });
        rows.sort((a, b) -> Long.compare((long) b.get("threads"), (long) a.get("threads")));
        return rows;
    }

    @GetMapping("/roles")
    public String roles(Model model) {
        model.addAttribute("studentLoginRequired", studentAccessPolicy.loginRequired());
        model.addAttribute("studentCount", studentService.enabledCount());
        model.addAttribute("pendingCount", studentService.pendingRequests().size());
        return "staff/roles";
    }

    /** Is the signed-in staff member an admin? */
    private boolean isAdmin() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    @GetMapping("/settings")
    public String settings(Model model) {        model.addAttribute("boards", boardRepository.findAll());
        model.addAttribute("windows", accessWindowRepository.findAll());
        model.addAttribute("closedMode", accessService.closedMode());
        model.addAttribute("rehearsal", staffSettingsService.rehearsalMode());
        model.addAttribute("livePollEnabled", staffSettingsService.livePollEnabled());
        model.addAttribute("studentLoginRequired", studentAccessPolicy.loginRequired());
        model.addAttribute("studentBlockReading", studentAccessPolicy.blockReading());
        model.addAttribute("autoBlock", moderationPolicy.autoBlockEnabled());
        model.addAttribute("hideEscalations", moderationPolicy.hideEscalations());
        model.addAttribute("policyIsAdminOnly", isAdmin());
        model.addAttribute("mediaBytes", mediaMaintenanceJob.mediaBytesOnDisk());
        model.addAttribute("mediaCount", mediaRepository.count());
        model.addAttribute("similarityBoost", siteSettingRepository.findById("similarity_boost")
                .map(com.schoolchan.board.SiteSetting::getValue).orElse("0.10"));
        model.addAttribute("trainingExamples", trainingExampleRepository.findAll());
>>>>>>> d5851d3 (Initial commit)
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

<<<<<<< HEAD
=======
    /**
     * Rewrites stored images at a sensible size and makes the small preview
     * copies. Staff can run this after a big upload session.
     */
    @PostMapping("/settings/shrink-media")
    public String shrinkMedia(@RequestParam(defaultValue = "200") int limit,
                             org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        long before = mediaMaintenanceJob.mediaBytesOnDisk();
        int done = mediaMaintenanceJob.shrinkBatch(Math.min(Math.max(limit, 1), 2000));
        long after = mediaMaintenanceJob.mediaBytesOnDisk();
        audit("MEDIA_SHRINK", done + " image(s), " + (before - after) + " bytes saved");
        redirectAttributes.addFlashAttribute("notice",
                "Shrank " + done + " image(s). Storage went from " + megabytes(before)
                        + " MB to " + megabytes(after) + " MB.");
        return "redirect:/staff/settings";
    }

    /**
     * Removes files in the media folder that no post refers to any more.
     * These are leftovers from deleted posts and take up space for nothing.
     */
    @PostMapping("/settings/tidy-media")
    public String tidyMedia(org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        long before = mediaMaintenanceJob.mediaBytesOnDisk();
        int removed = mediaMaintenanceJob.removeOrphans();
        long after = mediaMaintenanceJob.mediaBytesOnDisk();
        audit("MEDIA_TIDY", removed + " unused file(s), " + (before - after) + " bytes freed");
        redirectAttributes.addFlashAttribute("notice",
                "Removed " + removed + " unused file(s). Storage went from " + megabytes(before)
                        + " MB to " + megabytes(after) + " MB.");
        return "redirect:/staff/settings";
    }

    private long megabytes(long bytes) {
        return bytes / (1024 * 1024);
    }

    @PostMapping("/settings/moderation-policy")    public String moderationPolicy(@RequestParam(defaultValue = "false") boolean autoBlock,
                                   @RequestParam(defaultValue = "true") boolean hideEscalations) {
        moderationPolicy.setAutoBlockEnabled(autoBlock);
        moderationPolicy.setHideEscalations(hideEscalations);
        audit("MODERATION_POLICY", "autoBlock=" + autoBlock + " hideEscalations=" + hideEscalations);
        return "redirect:/staff/settings";
    }

    @PostMapping("/settings/student-login")
    public String studentLogin(@RequestParam(defaultValue = "false") boolean required,
                               @RequestParam(defaultValue = "false") boolean blockReading) {
        studentAccessPolicy.setLoginRequired(required);
        studentAccessPolicy.setBlockReading(blockReading);
        audit("STUDENT_LOGIN_REQUIRED", "required=" + required + " blockReading=" + blockReading);
        return "redirect:/staff/settings";
    }

    @PostMapping("/settings/rehearsal")
    public String rehearsal(@RequestParam(defaultValue = "false") boolean enabled) {
        staffSettingsService.setRehearsalMode(enabled);
        audit("ESCALATION_REHEARSAL", String.valueOf(enabled));
        return "redirect:/staff/settings";
    }

    @PostMapping("/settings/live-poll")
    public String livePoll(@RequestParam(defaultValue = "false") boolean enabled) {
        staffSettingsService.setLivePollEnabled(enabled);
        audit("LIVE_POLL", String.valueOf(enabled));
        return "redirect:/staff/settings";
    }

    @PostMapping("/settings/similarity")
    public String similarity(@RequestParam double boost) {
        double clamped = Math.max(0, Math.min(0.5, boost));
        siteSettingRepositorySet("similarity_boost", String.valueOf(clamped));
        audit("SIMILARITY_BOOST", String.valueOf(clamped));
        return "redirect:/staff/settings";
    }

    private void siteSettingRepositorySet(String key, String value) {
        var setting = siteSettingRepository.findById(key).orElseGet(com.schoolchan.board.SiteSetting::new);
        setting.setKey(key);
        setting.setValue(value);
        siteSettingRepository.save(setting);
    }

>>>>>>> d5851d3 (Initial commit)
    @PostMapping("/settings/rules")
    public String rules(@RequestParam String body) {
        staffSettingsService.saveRules(body);
        audit("RULES_UPDATE", "new version");
        return "redirect:/staff/settings";
    }

<<<<<<< HEAD
=======
    @PostMapping("/settings/rules/default")
    public String defaultRules() {
        staffSettingsService.saveRules(com.schoolchan.staff.DefaultRules.BODY);
        audit("RULES_UPDATE", "restored the shipped default text");
        return "redirect:/staff/settings";
    }

>>>>>>> d5851d3 (Initial commit)
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

<<<<<<< HEAD
=======
    @PostMapping("/settings/window/delete")
    public String deleteWindow(@RequestParam UUID id) {
        accessWindowRepository.deleteById(id);
        audit("ACCESS_WINDOW_DELETE", id.toString());
        return "redirect:/staff/settings";
    }

>>>>>>> d5851d3 (Initial commit)
    private void audit(String action, String detail) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setDetail(detail);
        auditLogRepository.save(log);
    }
}
