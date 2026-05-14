package com.onTrack;

public record SubmissionId(long value) {

    public SubmissionId {
        if (value <= 0) {
            throw new IllegalArgumentException("SubmissionId value must be positive");
        }
    }

    public static SubmissionId of(long value) {
        return new SubmissionId(value);
    }
}
