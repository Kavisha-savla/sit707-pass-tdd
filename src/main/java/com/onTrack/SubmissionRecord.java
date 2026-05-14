package com.onTrack;

import java.time.Instant;
import java.util.Objects;

public record SubmissionRecord(SubmissionId id, Submission submission, Instant storedAt) {

    public SubmissionRecord {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(submission, "submission must not be null");
        Objects.requireNonNull(storedAt, "storedAt must not be null");
    }
}
