package com.onTrack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubmissionValidatorTest {

    private final SubmissionValidator validator = new SubmissionValidator();

    private Submission validSubmission() {
        return new Submission(
                "225053119",
                "1.1P",
                "This is the body of my submission.",
                FileType.PDF,
                Instant.now()
        );
    }

    @Test
    @DisplayName("Cycle 1: a null submission is rejected with a clear error")
    void nullSubmissionIsRejected() {
        ValidationResult result = validator.validate(null);

        assertNotNull(result, "validator must never return null");
        assertFalse(result.isValid(), "null submission must be invalid");
        assertTrue(
                result.errors().stream().anyMatch(e -> e.toLowerCase().contains("submission")),
                "errors should mention the submission itself"
        );
    }

    @Test
    @DisplayName("Cycle 2: a null studentId is rejected")
    void nullStudentIdIsRejected() {
        Submission s = new Submission(null, "1.1P", "body", FileType.PDF, Instant.now());

        ValidationResult result = validator.validate(s);

        assertFalse(result.isValid(), "null studentId must be invalid");
        assertTrue(
                result.errors().stream().anyMatch(e -> e.toLowerCase().contains("studentid")),
                "errors should mention studentId"
        );
    }

    @Test
    @DisplayName("Cycle 2: a studentId that is not 9 digits is rejected")
    void wrongFormatStudentIdIsRejected() {
        Submission s = new Submission("12345", "1.1P", "body", FileType.PDF, Instant.now());

        ValidationResult result = validator.validate(s);

        assertFalse(result.isValid(), "5-digit studentId must be invalid");
        assertTrue(
                result.errors().stream().anyMatch(e -> e.toLowerCase().contains("studentid")),
                "errors should mention studentId"
        );
    }

    @Test
    @DisplayName("Cycle 2: a fully valid submission passes")
    void validSubmissionPasses() {
        ValidationResult result = validator.validate(validSubmission());

        assertTrue(result.isValid(), "a fully valid submission must pass");
    }

    @Test
    @DisplayName("Cycle 3: a null taskId is rejected")
    void nullTaskIdIsRejected() {
        Submission s = new Submission("225053119", null, "body", FileType.PDF, Instant.now());

        ValidationResult result = validator.validate(s);

        assertFalse(result.isValid());
        assertTrue(result.errors().stream().anyMatch(e -> e.toLowerCase().contains("taskid")));
    }

    @Test
    @DisplayName("Cycle 3: a taskId in the wrong format is rejected")
    void wrongFormatTaskIdIsRejected() {
        Submission s = new Submission("225053119", "TaskOne", "body", FileType.PDF, Instant.now());

        ValidationResult result = validator.validate(s);

        assertFalse(result.isValid());
        assertTrue(result.errors().stream().anyMatch(e -> e.toLowerCase().contains("taskid")));
    }

    @Test
    @DisplayName("Cycle 4: a null content is rejected")
    void nullContentIsRejected() {
        Submission s = new Submission("225053119", "1.1P", null, FileType.PDF, Instant.now());

        ValidationResult result = validator.validate(s);

        assertFalse(result.isValid());
        assertTrue(result.errors().stream().anyMatch(e -> e.toLowerCase().contains("content")));
    }

    @Test
    @DisplayName("Cycle 4: a blank content (whitespace only) is rejected")
    void blankContentIsRejected() {
        Submission s = new Submission("225053119", "1.1P", "   \n\t  ", FileType.PDF, Instant.now());

        ValidationResult result = validator.validate(s);

        assertFalse(result.isValid());
        assertTrue(result.errors().stream().anyMatch(e -> e.toLowerCase().contains("content")));
    }

    @Test
    @DisplayName("Cycle 4: content longer than 100000 characters is rejected")
    void oversizeContentIsRejected() {
        String huge = "a".repeat(100_001);
        Submission s = new Submission("225053119", "1.1P", huge, FileType.PDF, Instant.now());

        ValidationResult result = validator.validate(s);

        assertFalse(result.isValid());
        assertTrue(result.errors().stream().anyMatch(e -> e.toLowerCase().contains("content")));
    }

    @Test
    @DisplayName("Cycle 5: a null fileType is rejected")
    void nullFileTypeIsRejected() {
        Submission s = new Submission("225053119", "1.1P", "body", null, Instant.now());

        ValidationResult result = validator.validate(s);

        assertFalse(result.isValid());
        assertTrue(result.errors().stream().anyMatch(e -> e.toLowerCase().contains("filetype")));
    }

    @Test
    @DisplayName("Cycle 6: a null submittedAt is rejected")
    void nullSubmittedAtIsRejected() {
        Submission s = new Submission("225053119", "1.1P", "body", FileType.PDF, null);

        ValidationResult result = validator.validate(s);

        assertFalse(result.isValid());
        assertTrue(result.errors().stream().anyMatch(e -> e.toLowerCase().contains("submittedat")));
    }

    @Test
    @DisplayName("Cycle 6: a submittedAt more than 5 min in the future is rejected")
    void futureSubmittedAtIsRejected() {
        Instant farFuture = Instant.now().plusSeconds(60 * 10);
        Submission s = new Submission("225053119", "1.1P", "body", FileType.PDF, farFuture);

        ValidationResult result = validator.validate(s);

        assertFalse(result.isValid());
        assertTrue(result.errors().stream().anyMatch(e -> e.toLowerCase().contains("submittedat")));
    }
}
