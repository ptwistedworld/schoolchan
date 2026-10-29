package com.schoolchan.moderation;

import com.schoolchan.config.SchoolChanProperties;
import org.springframework.stereotype.Component;

import java.util.List;

<<<<<<< HEAD
=======
/**
 * Decides what happens to a post.
 *
 * <p>Policy: the board never removes anything on its own. A post is always
 * published; if any stage is unhappy with it, the post is <em>flagged</em> and
 * waits in the staff review queue, where a person decides. The only automatic
 * exception is the escalation category (a credible safety risk), which has its
 * own setting.
 */
>>>>>>> d5851d3 (Initial commit)
@Component
public class DecisionEngine {

    private final SchoolChanProperties properties;
<<<<<<< HEAD

    public DecisionEngine(SchoolChanProperties properties) {
        this.properties = properties;
    }

    public ModerationDecision decide(List<StageResult> stages, double reputationBoost) {
        boolean mlUnavailable = stages.stream().anyMatch(StageResult::isMlUnavailable);
        if (mlUnavailable) {
            return mapFailOnMl(properties.getModeration().getFailOnMlUnavailable());
        }

=======
    private final ModerationPolicy policy;

    public DecisionEngine(SchoolChanProperties properties, ModerationPolicy policy) {
        this.properties = properties;
        this.policy = policy;
    }

    public ModerationDecision decide(List<StageResult> stages, double reputationBoost) {
        // A stage that could not run simply contributes nothing. It never
        // hides a post, and the moderation result records that the check was
        // degraded so staff can see it.
>>>>>>> d5851d3 (Initial commit)
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

<<<<<<< HEAD
        if (score >= properties.getModeration().getBlockThreshold()) {
            return ModerationDecision.BLOCK;
        }
        if (score >= properties.getModeration().getQueueThreshold()) {
=======
        if (score >= properties.getModeration().getBlockThreshold()
                && policy.autoBlockEnabled()) {
            // Off by default: a machine does not remove content, a person does.
            return ModerationDecision.BLOCK;
        }
        if (score >= properties.getModeration().getQueueThreshold()) {
            // Flagged for a person to look at. The post is published.
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
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
=======
                r.contains("TOKEN_ESCALATE") || r.contains("ESCALATION")
                        || r.contains("self_harm") || r.startsWith("threat="));
>>>>>>> d5851d3 (Initial commit)
    }
}
