package com.onTrack;

import java.time.Instant;
import java.util.Objects;

public record ChatMessage(SubmissionId submissionId, String sender, String body, Instant sentAt) {

    public ChatMessage {
        Objects.requireNonNull(submissionId, "submissionId must not be null");
        Objects.requireNonNull(sender, "sender must not be null");
        Objects.requireNonNull(body, "body must not be null");
        Objects.requireNonNull(sentAt, "sentAt must not be null");
    }
}
