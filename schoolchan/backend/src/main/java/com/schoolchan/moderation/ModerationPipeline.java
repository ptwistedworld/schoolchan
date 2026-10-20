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
    private final ObjectMapper objectMapper;

    public ModerationPipeline(
            List<ModerationStage> stages,
            DecisionEngine decisionEngine,
            ModerationResultRepository resultRepository,
            EscalationRepository escalationRepository,
            ReputationService reputationService,
            SchoolChanProperties properties,
            ObjectMapper objectMapper) {
        this.stages = stages;
        this.decisionEngine = decisionEngine;
        this.resultRepository = resultRepository;
        this.escalationRepository = escalationRepository;
        this.reputationService = reputationService;
        this.properties = properties;
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
            case BLOCK -> PostStatus.BLOCKED;
            case QUEUE_FOR_REVIEW -> PostStatus.HELD_FOR_REVIEW;
            case ESCALATE -> PostStatus.HELD_FOR_REVIEW;
            case PUBLISH -> PostStatus.PUBLISHED;
        };
    }

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
