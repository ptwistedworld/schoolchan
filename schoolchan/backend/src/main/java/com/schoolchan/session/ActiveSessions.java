package com.schoolchan.session;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Counts the browsers that have been on the board in the last few minutes.
 * Nothing is stored beyond a salted session hash and a timestamp, and the
 * count is a number only - never a list.
 */
@Component
public class ActiveSessions {

    private static final long WINDOW_MINUTES = 5;

    private final Map<String, Instant> seen = new ConcurrentHashMap<>();

    public void touch(String sessionHash) {
        if (sessionHash == null || sessionHash.isBlank()) {
            return;
        }
        seen.put(sessionHash, Instant.now());
        prune();
    }

    /** How many distinct sessions were seen in the last few minutes. */
    public int activeCount() {
        prune();
        return seen.size();
    }

    private void prune() {
        Instant cutoff = Instant.now().minusSeconds(WINDOW_MINUTES * 60);
        seen.entrySet().removeIf(e -> e.getValue().isBefore(cutoff));
    }
}
