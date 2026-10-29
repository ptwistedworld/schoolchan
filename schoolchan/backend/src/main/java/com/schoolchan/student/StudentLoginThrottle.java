package com.schoolchan.student;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Counts failed student sign-ins per student ID and blocks further attempts
 * for a short cool-off period.
 *
 * <p>Kept in memory on purpose: a restart clears the counters, and the point
 * is to slow down a guessing attack, not to build a permanent record of it.
 * Nothing here is written to the database, so no failed-attempt data about a
 * student is retained.
 */
@Component
public class StudentLoginThrottle {

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public static class Attempt {
        int failures;
        long lockedUntil;
    }

    /**
     * Records a failed attempt. Returns true when the account is now locked.
     */
    public boolean recordFailure(String studentId, int maxAttempts, long lockoutMinutes) {
        if (studentId == null || studentId.isBlank()) {
            return false;
        }
        String key = studentId.trim().toLowerCase();
        Attempt a = attempts.computeIfAbsent(key, k -> new Attempt());
        synchronized (a) {
            long now = System.currentTimeMillis();
            if (a.lockedUntil > now) {
                return true;
            }
            if (a.lockedUntil != 0 && a.lockedUntil <= now) {
                a.failures = 0;
                a.lockedUntil = 0;
            }
            a.failures++;
            if (a.failures >= maxAttempts) {
                a.lockedUntil = now + lockoutMinutes * 60_000L;
                a.failures = 0;
                return true;
            }
        }
        return false;
    }

    /** Clears the counter after a successful sign-in. */
    public void recordSuccess(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            return;
        }
        attempts.remove(studentId.trim().toLowerCase());
    }

    /** True when this student ID is currently locked out. */
    public boolean isLocked(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            return false;
        }
        Attempt a = attempts.get(studentId.trim().toLowerCase());
        if (a == null) {
            return false;
        }
        synchronized (a) {
            return a.lockedUntil > System.currentTimeMillis();
        }
    }

    /** How many attempts are left before the lock-out kicks in. */
    public int remainingAttempts(String studentId, int maxAttempts) {
        if (studentId == null || studentId.isBlank()) {
            return maxAttempts;
        }
        Attempt a = attempts.get(studentId.trim().toLowerCase());
        if (a == null) {
            return maxAttempts;
        }
        synchronized (a) {
            if (a.lockedUntil > System.currentTimeMillis()) {
                return 0;
            }
            return Math.max(0, maxAttempts - a.failures);
        }
    }
}
