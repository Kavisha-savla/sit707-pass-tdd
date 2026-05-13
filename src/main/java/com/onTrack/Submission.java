package com.onTrack;

import java.time.Instant;

public record Submission(
        String studentId,
        String taskId,
        String content,
        FileType fileType,
        Instant submittedAt
) {
}
