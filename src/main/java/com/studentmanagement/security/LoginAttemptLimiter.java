package com.studentmanagement.security;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** Small per-process IP limiter for login guessing; it does not trust proxy headers. */
public final class LoginAttemptLimiter {

    private static final int MAX_FAILURES = 5;
    private static final long WINDOW_MILLIS = 15 * 60 * 1000L;
    private static final long LOCK_MILLIS = 15 * 60 * 1000L;
    private static final int MAX_ENTRIES = 10_000;

    private final Map<String, Attempt> attempts = new HashMap<>();

    public synchronized boolean isBlocked(String address) {
        long now = System.currentTimeMillis();
        Attempt attempt = attempts.get(normalize(address));
        if (attempt == null) {
            return false;
        }
        if (attempt.lockedUntil > now) {
            return true;
        }
        if (now - attempt.windowStarted > WINDOW_MILLIS) {
            attempts.remove(normalize(address));
        }
        return false;
    }

    public synchronized void recordFailure(String address) {
        long now = System.currentTimeMillis();
        String key = normalize(address);
        Attempt attempt = attempts.get(key);
        if (attempt == null || now - attempt.windowStarted > WINDOW_MILLIS) {
            attempt = new Attempt(now);
            attempts.put(key, attempt);
        }
        attempt.failures++;
        if (attempt.failures >= MAX_FAILURES) {
            attempt.lockedUntil = now + LOCK_MILLIS;
        }
        prune(now);
    }

    public synchronized void recordSuccess(String address) {
        attempts.remove(normalize(address));
    }

    private void prune(long now) {
        if (attempts.size() <= MAX_ENTRIES) {
            return;
        }
        Iterator<Map.Entry<String, Attempt>> iterator = attempts.entrySet().iterator();
        while (iterator.hasNext() && attempts.size() > MAX_ENTRIES / 2) {
            Map.Entry<String, Attempt> entry = iterator.next();
            if (now - entry.getValue().windowStarted > WINDOW_MILLIS) {
                iterator.remove();
            }
        }
        iterator = attempts.entrySet().iterator();
        while (iterator.hasNext() && attempts.size() > MAX_ENTRIES) {
            iterator.next();
            iterator.remove();
        }
    }

    private String normalize(String address) {
        return address == null || address.isEmpty() ? "unknown" : address;
    }

    private static final class Attempt {
        private final long windowStarted;
        private int failures;
        private long lockedUntil;

        private Attempt(long windowStarted) {
            this.windowStarted = windowStarted;
        }
    }
}
