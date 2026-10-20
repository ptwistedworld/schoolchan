package com.schoolchan.board;

import com.schoolchan.config.SchoolChanProperties;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PostRateLimiter {

    private final SchoolChanProperties properties;
    private final Map<String, Deque<Instant>> buckets = new ConcurrentHashMap<>();

    public PostRateLimiter(SchoolChanProperties properties) {
        this.properties = properties;
    }

    public boolean allow(String sessionHash) {
        int limit = properties.getRateLimit().getPostsPerMinute();
        Deque<Instant> deque = buckets.computeIfAbsent(sessionHash, k -> new ArrayDeque<>());
        synchronized (deque) {
            Instant cutoff = Instant.now().minusSeconds(60);
            while (!deque.isEmpty() && deque.peekFirst().isBefore(cutoff)) {
                deque.pollFirst();
            }
            if (deque.size() >= limit) {
                return false;
            }
            deque.addLast(Instant.now());
            return true;
        }
    }
}
