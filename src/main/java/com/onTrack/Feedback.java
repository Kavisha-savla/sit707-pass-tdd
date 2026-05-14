package com.onTrack;

import java.time.Instant;
import java.util.Objects;

public record Feedback(SubmissionId submissionId, FeedbackStatus status, String comment, Instant setAt) {

    public Feedback {
        Objects.requireNonNull(submissionId, "submissionId must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(comment, "comment must not be null");
        Objects.requireNonNull(setAt, "setAt must not be null");
    }
}
