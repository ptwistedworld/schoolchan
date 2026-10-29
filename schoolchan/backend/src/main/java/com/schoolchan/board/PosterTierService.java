package com.schoolchan.board;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Anonymous trust tiers.
 *
 * <p>A session earns trust when staff confirm its reports, and loses it when
 * its posts are removed. The tier is capped so no single session can ever
 * dominate, and it is never shown to anyone — it only nudges the decision
 * engine and adds a small score bonus for trusted posters' confirmed reports.
 */
@Service
public class PosterTierService {

    /** Nobody can be better than this, whatever they do. */
    public static final double MAX_SCORE = 3.0;
    private static final double CONFIRMED_REPORT_BONUS = 0.5;
    private static final double REMOVED_POST_PENALTY = 1.0;

    private final PosterTierRepository tiers;

    public PosterTierService(PosterTierRepository tiers) {
        this.tiers = tiers;
    }

    @Transactional
    public void recordConfirmedReport(String sessionHash) {
        adjust(sessionHash, CONFIRMED_REPORT_BONUS);
    }

    @Transactional
    public void recordRemovedPost(String sessionHash) {
        adjust(sessionHash, -REMOVED_POST_PENALTY);
    }

    private void adjust(String sessionHash, double delta) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return;
        }
        PosterTier tier = tiers.findBySessionHash(sessionHash).orElseGet(() -> {
            PosterTier t = new PosterTier();
            t.setId(java.util.UUID.randomUUID());
            t.setSessionHash(sessionHash);
            return t;
        });
        double score = Math.max(0, Math.min(MAX_SCORE, tier.getScore() + delta));
        tier.setScore(score);
        tier.setTier(tierFor(score));
        tier.setUpdatedAt(Instant.now());
        tiers.save(tier);
    }

    /** NEW -> REGULAR -> TRUSTED -> VETERAN, capped at VETERAN. */
    public static String tierFor(double score) {
        if (score >= 3.0) return "VETERAN";
        if (score >= 1.5) return "TRUSTED";
        if (score >= 0.5) return "REGULAR";
        return "NEW";
    }

    @Transactional(readOnly = true)
    public String tierOf(String sessionHash) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return "NEW";
        }
        return tiers.findBySessionHash(sessionHash).map(PosterTier::getTier).orElse("NEW");
    }

    @Transactional(readOnly = true)
    public Optional<PosterTier> find(String sessionHash) {
        return tiers.findBySessionHash(sessionHash);
    }

    @Transactional(readOnly = true)
    public List<PosterTier> all() {
        return tiers.findAllByOrderByScoreDesc();
    }
}
