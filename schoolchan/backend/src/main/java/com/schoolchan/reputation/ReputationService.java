package com.schoolchan.reputation;

import com.schoolchan.config.SchoolChanProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class ReputationService {

    private static final double MIN = 0.1;
    private static final double MAX = 2.0;

    private final ReputationEntryRepository repository;
    private final SchoolChanProperties properties;

    public ReputationService(ReputationEntryRepository repository, SchoolChanProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public double reputationBoost(String sessionHash) {
        return repository.findBySessionHash(sessionHash)
                .map(e -> {
                    double delta = (1.0 - e.getReliability()) * properties.getModeration().getReputationBoostCap();
                    if (strikeActive(e)) {
                        delta += Math.min(0.1, e.getStrikeLevel() * 0.05);
                    }
                    return Math.min(properties.getModeration().getReputationBoostCap(), Math.max(0, delta));
                })
                .orElse(0.0);
    }

    @Transactional(readOnly = true)
    public boolean shouldAutoHold(String sessionHash) {
        return repository.findBySessionHash(sessionHash)
                .map(e -> e.getReliability() < 0.5 || strikeActive(e))
                .orElse(false);
    }

<<<<<<< HEAD
=======
    /**
     * A plain sentence about a session's trust, for staff. A moderator should
     * not have to guess what "low trust score" means.
     */
    @Transactional(readOnly = true)
    public String trustSummary(String sessionHash) {
        return repository.findBySessionHash(sessionHash)
                .map(e -> {
                    long strikes = e.getStrikeLevel();
                    return "trust score " + Math.round(e.getReliability() * 100) / 100.0
                            + " of 1.0, " + strikes + " removal" + (strikes == 1 ? "" : "s")
                            + " on record"
                            + (strikeActive(e) ? " (a strike is active right now)" : "");
                })
                .orElse("no history for this browser");
    }

>>>>>>> d5851d3 (Initial commit)
    @Transactional
    public void recordStrike(String sessionHash, Instant expiresAt) {
        ReputationEntry entry = getOrCreate(sessionHash);
        entry.setStrikeLevel(entry.getStrikeLevel() + 1);
        entry.setStrikeExpiresAt(expiresAt);
        entry.setReliability(clamp(entry.getReliability() - 0.15));
        entry.setUpdatedAt(Instant.now());
        repository.save(entry);
    }

    @Transactional
    public ReputationEntry getOrCreate(String sessionHash) {
        return repository.findBySessionHash(sessionHash).orElseGet(() -> {
            ReputationEntry entry = new ReputationEntry();
            entry.setId(UUID.randomUUID());
            entry.setSessionHash(sessionHash);
            entry.setReliability(1.0);
            entry.setUpdatedAt(Instant.now());
            return repository.save(entry);
        });
    }

    @Transactional
    public void confirmReport(String sessionHash) {
        // Confirmed reports raise reporter reliability (Slashdot-style).
        adjust(sessionHash, 0.1);
    }

    @Transactional
    public void rejectReport(String sessionHash) {
        adjust(sessionHash, -0.05);
    }

    private void adjust(String sessionHash, double delta) {
        ReputationEntry entry = getOrCreate(sessionHash);
        entry.setReliability(clamp(entry.getReliability() + delta));
        entry.setUpdatedAt(Instant.now());
        repository.save(entry);
    }

    private boolean strikeActive(ReputationEntry entry) {
        return entry.getStrikeLevel() > 0
                && entry.getStrikeExpiresAt() != null
                && entry.getStrikeExpiresAt().isAfter(Instant.now());
    }

    private double clamp(double value) {
        return Math.max(MIN, Math.min(MAX, value));
    }
}
