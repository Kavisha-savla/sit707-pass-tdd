package com.onTrack;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FeedbackService {

    private final Clock clock;
    private final Map<Long, Feedback> store = new ConcurrentHashMap<>();

    public FeedbackService() {
        this(Clock.systemUTC());
    }

    public FeedbackService(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public Feedback setStatus(SubmissionId id, FeedbackStatus status, String comment) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(comment, "comment must not be null");

        Feedback feedback = new Feedback(id, status, comment, Instant.now(clock));
        store.put(id.value(), feedback);
        return feedback;
    }

    public Optional<Feedback> get(SubmissionId id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id.value()));
    }
}
