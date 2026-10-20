package com.schoolchan.moderation;

import java.util.ArrayList;
import java.util.List;

public class StageResult {

    private final String stage;
    private final double score;
    private final List<String> reasons = new ArrayList<>();
    private final long latencyMs;
    private final boolean mlUnavailable;

    public StageResult(String stage, double score, long latencyMs) {
        this(stage, score, latencyMs, false);
    }

    public StageResult(String stage, double score, long latencyMs, boolean mlUnavailable) {
        this.stage = stage;
        this.score = score;
        this.latencyMs = latencyMs;
        this.mlUnavailable = mlUnavailable;
    }

    public String getStage() {
        return stage;
    }

    public double getScore() {
        return score;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public boolean isMlUnavailable() {
        return mlUnavailable;
    }

    public StageResult withReason(String reason) {
        reasons.add(reason);
        return this;
    }
}
