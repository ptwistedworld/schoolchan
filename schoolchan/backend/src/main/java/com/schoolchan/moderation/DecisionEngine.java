package com.schoolchan.moderation;

import com.schoolchan.config.SchoolChanProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DecisionEngine {

    private final SchoolChanProperties properties;

    public DecisionEngine(SchoolChanProperties properties) {
        this.properties = properties;
    }

    public ModerationDecision decide(List<StageResult> stages, double reputationBoost) {
        boolean mlUnavailable = stages.stream().anyMatch(StageResult::isMlUnavailable);
        if (mlUnavailable) {
            return mapFailOnMl(properties.getModeration().getFailOnMlUnavailable());
        }

        double escalationScore = stages.stream()
                .filter(this::isEscalationStage)
                .mapToDouble(StageResult::getScore)
                .max()
                .orElse(0);
        boolean escalateKeyword = stages.stream()
                .flatMap(s -> s.getReasons().stream())
                .anyMatch(r -> r.contains("TOKEN_ESCALATE") || r.contains("ESCALATION"));
        if (escalateKeyword || escalationScore >= properties.getModeration().getEscalationThreshold()) {
            return ModerationDecision.ESCALATE;
        }

        double score = stages.stream()
                .filter(s -> !isEscalationStage(s))
                .mapToDouble(StageResult::getScore)
                .max()
                .orElse(0);
        score = Math.min(1.0, score + Math.max(0, reputationBoost));

        if (score >= properties.getModeration().getBlockThreshold()) {
            return ModerationDecision.BLOCK;
        }
        if (score >= properties.getModeration().getQueueThreshold()) {
            return ModerationDecision.QUEUE_FOR_REVIEW;
        }
        return ModerationDecision.PUBLISH;
    }

    private boolean isEscalationStage(StageResult stage) {
        String name = stage.getStage() == null ? "" : stage.getStage().toLowerCase();
        if (name.contains("escalat") || name.contains("self_harm") || name.contains("threat")) {
            return true;
        }
        return stage.getReasons().stream().anyMatch(r ->
                r.contains("TOKEN_ESCALATE") || r.contains("ESCALATION") || r.contains("self_harm"));
    }

    private ModerationDecision mapFailOnMl(String setting) {
        if ("BLOCK".equalsIgnoreCase(setting)) {
            return ModerationDecision.BLOCK;
        }
        if ("PUBLISH".equalsIgnoreCase(setting)) {
            return ModerationDecision.PUBLISH;
        }
        return ModerationDecision.QUEUE_FOR_REVIEW;
    }
}
