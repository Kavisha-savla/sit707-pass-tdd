package com.onTrack;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TaskService {

    private final SubmissionValidator validator;
    private final Clock clock;
    private final AtomicLong nextId = new AtomicLong(1);
    private final Map<Long, SubmissionRecord> store = new ConcurrentHashMap<>();

    public TaskService() {
        this(new SubmissionValidator(), Clock.systemUTC());
    }

    public TaskService(SubmissionValidator validator, Clock clock) {
        this.validator = Objects.requireNonNull(validator, "validator must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public SubmissionId submit(Submission submission) {
        ValidationResult result = validator.validate(submission);
        if (!result.isValid()) {
            throw new ValidationException(result.errors());
        }
        SubmissionId id = SubmissionId.of(nextId.getAndIncrement());
        store.put(id.value(), new SubmissionRecord(id, submission, Instant.now(clock)));
        return id;
    }

    public List<SubmissionRecord> inbox(String studentId) {
        if (studentId == null) {
            return List.of();
        }
        List<SubmissionRecord> matches = new ArrayList<>();
        for (SubmissionRecord record : store.values()) {
            if (studentId.equals(record.submission().studentId())) {
                matches.add(record);
            }
        }
        return matches;
    }

    public Optional<SubmissionRecord> find(SubmissionId id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id.value()));
    }
}
