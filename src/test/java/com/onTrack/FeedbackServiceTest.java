package com.onTrack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeedbackServiceTest {

    private FeedbackService service;

    @BeforeEach
    void setUp() {
        service = new FeedbackService();
    }

    @Test
    @DisplayName("R: setStatus then get returns the same status and comment")
    void R_setStatusThenGetReturnsSameValues() {
        SubmissionId id = SubmissionId.of(1L);

        service.setStatus(id, FeedbackStatus.FEEDBACK_GIVEN, "Good work, fix references.");
        Optional<Feedback> retrieved = service.get(id);

        assertTrue(retrieved.isPresent(), "feedback must be retrievable after setStatus");
        assertEquals(FeedbackStatus.FEEDBACK_GIVEN, retrieved.get().status());
        assertEquals("Good work, fix references.", retrieved.get().comment());
    }

    @Test
    @DisplayName("B: setStatus with an empty-string comment is allowed")
    void B_setStatusWithEmptyCommentIsAllowed() {
        SubmissionId id = SubmissionId.of(2L);

        Feedback feedback = service.setStatus(id, FeedbackStatus.PENDING, "");

        assertNotNull(feedback);
        assertEquals("", feedback.comment(), "empty-string comment must be preserved");
        assertTrue(service.get(id).isPresent(), "feedback with empty comment must still be stored");
    }

    @Test
    @DisplayName("I: get(setStatus(...)) returns equivalent feedback (inverse round-trip)")
    void I_setStatusThenGetIsInverse() {
        SubmissionId id = SubmissionId.of(3L);

        Feedback written = service.setStatus(id, FeedbackStatus.COMPLETE, "All good.");
        Feedback read = service.get(id).orElseThrow();

        assertEquals(written, read, "written and read feedback must be equal");
    }

    @Test
    @DisplayName("C: after N writes the last value wins (cross-check against expected)")
    void C_lastSetStatusWinsCrossCheck() {
        SubmissionId id = SubmissionId.of(4L);
        FeedbackStatus expectedLast = null;
        String expectedComment = null;

        FeedbackStatus[] sequence = {
                FeedbackStatus.PENDING,
                FeedbackStatus.IN_REVIEW,
                FeedbackStatus.FEEDBACK_GIVEN,
                FeedbackStatus.RESUBMIT,
                FeedbackStatus.COMPLETE
        };
        for (int i = 0; i < sequence.length; i++) {
            expectedLast = sequence[i];
            expectedComment = "comment " + i;
            service.setStatus(id, expectedLast, expectedComment);
        }

        Feedback retrieved = service.get(id).orElseThrow();
        assertEquals(expectedLast, retrieved.status(), "last status must win");
        assertEquals(expectedComment, retrieved.comment(), "last comment must win");
    }

    @Test
    @DisplayName("E: get of an unknown id returns Optional.empty")
    void E_getOfUnknownReturnsEmpty() {
        Optional<Feedback> retrieved = service.get(SubmissionId.of(9_999_999L));

        assertTrue(retrieved.isEmpty(), "unknown id must yield empty Optional");
    }

    @Test
    @DisplayName("P: setStatus 10000 times then a final get completes under 200ms")
    void P_setStatus10kThenGetUnder200ms() {
        for (long i = 1; i <= 10_000; i++) {
            service.setStatus(SubmissionId.of(i), FeedbackStatus.IN_REVIEW, "c" + i);
        }

        assertTimeoutPreemptively(Duration.ofMillis(200), () -> {
            for (long i = 1; i <= 10_000; i++) {
                assertTrue(service.get(SubmissionId.of(i)).isPresent());
            }
        });
    }
}
