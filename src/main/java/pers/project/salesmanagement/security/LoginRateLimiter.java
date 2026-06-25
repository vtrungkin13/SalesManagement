package pers.project.salesmanagement.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final int WINDOW_SECONDS = 60;

    private final ConcurrentHashMap<String, Deque<Instant>> ipAttempts = new ConcurrentHashMap<>();

    public boolean isAllowed(String ip) {
        Instant now = Instant.now();
        Instant limitTime = now.minusSeconds(WINDOW_SECONDS);

        Deque<Instant> attempts = ipAttempts.computeIfAbsent(ip, k -> new ConcurrentLinkedDeque<>());

        // Remove old attempts outside of the sliding window
        while (!attempts.isEmpty() && attempts.peekFirst().isBefore(limitTime)) {
            attempts.pollFirst();
        }

        if (attempts.size() >= MAX_ATTEMPTS) {
            return false;
        }

        attempts.addLast(now);
        return true;
    }
}
