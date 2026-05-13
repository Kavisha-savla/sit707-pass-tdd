package com.onTrack;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SubmissionValidator {

    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^\\d{9}$");
    private static final Pattern TASK_ID_PATTERN = Pattern.compile("^[1-9]\\.[1-9][PCDH]$");
    private static final int MAX_CONTENT_LENGTH = 100_000;
    private static final Duration FUTURE_SKEW_TOLERANCE = Duration.ofMinutes(5);

    public ValidationResult validate(Submission submission) {
        List<String> errors = new ArrayList<>();

        if (submission == null) {
            errors.add("submission must not be null");
            return ValidationResult.failed(errors);
        }

        validateStudentId(submission.studentId(), errors);
        validateTaskId(submission.taskId(), errors);
        validateContent(submission.content(), errors);
        validateFileType(submission.fileType(), errors);
        validateSubmittedAt(submission.submittedAt(), errors);

        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.failed(errors);
    }

    private void validateStudentId(String studentId, List<String> errors) {
        if (studentId == null) {
            errors.add("studentId must not be null");
            return;
        }
        if (!STUDENT_ID_PATTERN.matcher(studentId).matches()) {
            errors.add("studentId must be exactly 9 digits");
        }
    }

    private void validateTaskId(String taskId, List<String> errors) {
        if (taskId == null) {
            errors.add("taskId must not be null");
            return;
        }
        if (!TASK_ID_PATTERN.matcher(taskId).matches()) {
            errors.add("taskId must match the pattern N.NX where X is one of P, C, D, H (e.g. 1.1P)");
        }
    }

    private void validateContent(String content, List<String> errors) {
        if (content == null) {
            errors.add("content must not be null");
            return;
        }
        if (content.isBlank()) {
            errors.add("content must not be blank");
            return;
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            errors.add("content must be at most " + MAX_CONTENT_LENGTH + " characters");
        }
    }

    private void validateFileType(FileType fileType, List<String> errors) {
        if (fileType == null) {
            errors.add("fileType must not be null");
        }
    }

    private void validateSubmittedAt(Instant submittedAt, List<String> errors) {
        if (submittedAt == null) {
            errors.add("submittedAt must not be null");
            return;
        }
        Instant cutoff = Instant.now().plus(FUTURE_SKEW_TOLERANCE);
        if (submittedAt.isAfter(cutoff)) {
            errors.add("submittedAt must not be more than 5 minutes in the future");
        }
    }
}
