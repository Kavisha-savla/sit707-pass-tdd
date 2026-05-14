package com.onTrack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BranchCoverageTopUpTest {

    @Test
    @DisplayName("E: ValidationResult.failed(null) throws IllegalArgumentException")
    void validationResultFailedWithNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationResult.failed(null));
    }

    @Test
    @DisplayName("E: ValidationResult.failed(emptyList) throws IllegalArgumentException")
    void validationResultFailedWithEmptyThrows() {
        assertThrows(IllegalArgumentException.class, () -> ValidationResult.failed(List.of()));
    }

    @Test
    @DisplayName("B: TaskService.inbox(null) returns an empty list")
    void taskServiceInboxNullReturnsEmpty() {
        TaskService service = new TaskService();
        assertTrue(service.inbox(null).isEmpty(), "null studentId must yield empty inbox");
    }

    @Test
    @DisplayName("B: TaskService.find(null) returns Optional.empty")
    void taskServiceFindNullReturnsEmpty() {
        TaskService service = new TaskService();
        assertTrue(service.find(null).isEmpty(), "null id must yield empty Optional");
    }

    @Test
    @DisplayName("B: FeedbackService.get(null) returns Optional.empty")
    void feedbackServiceGetNullReturnsEmpty() {
        FeedbackService service = new FeedbackService();
        assertTrue(service.get(null).isEmpty(), "null id must yield empty Optional");
    }

    @Test
    @DisplayName("B: ChatService.list(null) returns an empty list")
    void chatServiceListNullReturnsEmpty() {
        ChatService service = new ChatService();
        assertTrue(service.list(null).isEmpty(), "null id must yield empty list");
    }

    @Test
    @DisplayName("B: ChatService.list(unknown id) returns an empty list")
    void chatServiceListUnknownIdReturnsEmpty() {
        ChatService service = new ChatService();
        assertTrue(service.list(SubmissionId.of(999_999L)).isEmpty(),
                "unknown id with no posts must yield empty list");
    }

    @Test
    @DisplayName("E: SubmissionId of zero throws IllegalArgumentException")
    void submissionIdZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> SubmissionId.of(0L));
    }

    @Test
    @DisplayName("E: SubmissionId of a negative value throws IllegalArgumentException")
    void submissionIdNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> SubmissionId.of(-7L));
    }

    @Test
    @DisplayName("R: ValidationException with errors carries them on the message and accessor")
    void validationExceptionWithErrorsCarriesThem() {
        ValidationException ex = new ValidationException(List.of("studentId bad", "taskId bad"));

        assertEquals(2, ex.errors().size());
        assertTrue(ex.getMessage().contains("studentId bad"), "message must include first error");
        assertTrue(ex.getMessage().contains("taskId bad"), "message must include second error");
    }

    @Test
    @DisplayName("E: ValidationException with null errors rejects via NullPointerException")
    void validationExceptionWithNullErrorsRejected() {
        assertThrows(NullPointerException.class, () -> new ValidationException(null));
    }

    @Test
    @DisplayName("B: ValidationException with empty errors falls back to default message")
    void validationExceptionWithEmptyErrorsHasDefaultMessage() {
        ValidationException ex = new ValidationException(List.of());

        assertNotNull(ex.getMessage(), "message must never be null");
        assertEquals("validation failed", ex.getMessage(), "empty errors yield default message");
        assertTrue(ex.errors().isEmpty(), "empty errors must round-trip empty");
    }
}
