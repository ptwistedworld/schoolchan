package com.schoolchan.moderation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolchan.board.Post;
import com.schoolchan.board.PostStatus;
import com.schoolchan.config.SchoolChanProperties;
import com.schoolchan.reputation.ReputationService;
import com.schoolchan.staff.Escalation;
import com.schoolchan.staff.EscalationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ModerationPipeline {

    private final List<ModerationStage> stages;
    private final DecisionEngine decisionEngine;
    private final ModerationResultRepository resultRepository;
    private final EscalationRepository escalationRepository;
    private final ReputationService reputationService;
    private final SchoolChanProperties properties;
<<<<<<< HEAD
=======
    private final ModerationPolicy policy;
>>>>>>> d5851d3 (Initial commit)
    private final ObjectMapper objectMapper;

    public ModerationPipeline(
            List<ModerationStage> stages,
            DecisionEngine decisionEngine,
            ModerationResultRepository resultRepository,
            EscalationRepository escalationRepository,
            ReputationService reputationService,
            SchoolChanProperties properties,
<<<<<<< HEAD
=======
            ModerationPolicy policy,
>>>>>>> d5851d3 (Initial commit)
            ObjectMapper objectMapper) {
        this.stages = stages;
        this.decisionEngine = decisionEngine;
        this.resultRepository = resultRepository;
        this.escalationRepository = escalationRepository;
        this.reputationService = reputationService;
        this.properties = properties;
<<<<<<< HEAD
=======
        this.policy = policy;
>>>>>>> d5851d3 (Initial commit)
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PostStatus screen(Post post, String sessionHash) {
        String combined = post.getOriginalText();
        if (post.getTranslatedText() != null && !post.getTranslatedText().isBlank()) {
            combined = combined + "\n" + post.getTranslatedText();
        }

        List<StageResult> results = new ArrayList<>();
        long totalMs = 0;
        boolean mlUnavailable = false;
        List<ModerationStage> activeStages = selectStages();
        for (ModerationStage stage : activeStages) {
            StageResult r = stage.evaluate(post, combined);
            results.add(r);
            totalMs += r.getLatencyMs();
            mlUnavailable |= r.isMlUnavailable();
        }

        double boost = reputationService.reputationBoost(sessionHash);
        ModerationDecision decision = decisionEngine.decide(results, boost);
        persistResult(post.getId(), decision, results, totalMs, mlUnavailable);

        if (decision == ModerationDecision.ESCALATE) {
            createEscalation(post);
        }

        return switch (decision) {
<<<<<<< HEAD
            case BLOCK -> PostStatus.BLOCKED;
            case QUEUE_FOR_REVIEW -> PostStatus.HELD_FOR_REVIEW;
            case ESCALATE -> PostStatus.HELD_FOR_REVIEW;
=======
            // A machine never removes anything. The post stays up and is
            // flagged so a person can look at it.
            case BLOCK -> {
                post.setStatus(PostStatus.PUBLISHED);
                post.setFlagged(true);
                post.setFlagSource("filter");
                post.setFlagReason(flagReason(results));
                yield PostStatus.PUBLISHED;
            }
            case QUEUE_FOR_REVIEW -> {
                post.setStatus(PostStatus.PUBLISHED);
                post.setFlagged(true);
                post.setFlagSource("filter");
                post.setFlagReason(flagReason(results));
                yield PostStatus.PUBLISHED;
            }
            case ESCALATE -> {
                boolean hide = policy.hideEscalations();
                post.setStatus(hide ? PostStatus.HELD_FOR_REVIEW : PostStatus.PUBLISHED);
                post.setFlagged(true);
                post.setFlagSource("escalation");
                post.setFlagReason(flagReason(results));
                yield post.getStatus();
            }
>>>>>>> d5851d3 (Initial commit)
            case PUBLISH -> PostStatus.PUBLISHED;
        };
    }

<<<<<<< HEAD
=======
    /** A short, plain summary of why a post was flagged, for staff. */
    private String flagReason(List<StageResult> results) {
        List<String> reasons = new ArrayList<>();
        for (StageResult r : results) {
            for (String reason : r.getReasons()) {
                if (!reasons.contains(reason)) {
                    reasons.add(reason);
                }
            }
        }
        if (reasons.isEmpty()) {
            return "scored above the review threshold";
        }
        return String.join("; ", reasons);
    }

>>>>>>> d5851d3 (Initial commit)
    private List<ModerationStage> selectStages() {
        if (properties.getModeration().isBaselineOnly()) {
            return stages.stream()
                    .filter(s -> "keyword".equals(s.name()) || "keyword-blacklist".equals(s.name()))
                    .toList();
        }
        return stages;
    }

    private void persistResult(
            UUID postId,
            ModerationDecision decision,
            List<StageResult> results,
            long totalMs,
            boolean mlUnavailable) {
        ModerationResult entity = new ModerationResult();
        entity.setId(UUID.randomUUID());
        entity.setPostId(postId);
        entity.setDecision(decision);
        entity.setReasons(results.stream()
                .flatMap(r -> r.getReasons().stream())
                .collect(Collectors.joining(";")));
        try {
            entity.setStageResultsJson(objectMapper.writeValueAsString(results));
        } catch (JsonProcessingException e) {
            entity.setStageResultsJson("[]");
        }
        entity.setTotalLatencyMs(totalMs);
        entity.setBaselineMode(properties.getModeration().isBaselineOnly());
        entity.setMlUnavailable(mlUnavailable);
        resultRepository.save(entity);
    }

    private void createEscalation(Post post) {
        Escalation escalation = new Escalation();
        escalation.setId(UUID.randomUUID());
        escalation.setPostId(post.getId());
        escalation.setCategory("AUTO_ESCALATION");
        escalation.setStatus("NEW");
        escalationRepository.save(escalation);
    }
}
