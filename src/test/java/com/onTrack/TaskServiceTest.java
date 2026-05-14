package com.onTrack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskServiceTest {

    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService();
    }

    private Submission validSubmission(String studentId) {
        return new Submission(studentId, "1.1P", "body of submission", FileType.PDF, Instant.now());
    }

    @Test
    @DisplayName("R: submit returns a non-null SubmissionId and stores the record")
    void R_submitValidReturnsId() {
        SubmissionId id = service.submit(validSubmission("225053119"));

        assertNotNull(id, "submit must return a non-null id");
        assertTrue(id.value() > 0, "id value must be positive");
        assertTrue(service.find(id).isPresent(), "stored record must be retrievable by id");
    }

    @Test
    @DisplayName("B: inbox of a student with no submissions returns an empty list")
    void B_inboxOfNewStudentIsEmpty() {
        List<SubmissionRecord> inbox = service.inbox("225053119");

        assertNotNull(inbox, "inbox must never return null");
        assertTrue(inbox.isEmpty(), "fresh student must have empty inbox");
    }

    @Test
    @DisplayName("I: submit then find returns the same submission (inverse)")
    void I_submitThenFindRoundTrip() {
        Submission original = validSubmission("225053119");

        SubmissionId id = service.submit(original);
        Optional<SubmissionRecord> found = service.find(id);

        assertTrue(found.isPresent(), "find must return the stored record");
        assertEquals(original, found.get().submission(), "find must return the same submission that was submitted");
    }

    @Test
    @DisplayName("C: inbox size matches an explicit submit counter (cross-check)")
    void C_inboxSizeMatchesExplicitCounter() {
        int submitted = 0;
        for (int i = 0; i < 25; i++) {
            service.submit(validSubmission("225053119"));
            submitted++;
        }

        assertEquals(submitted, service.inbox("225053119").size(),
                "inbox size must equal the number of submissions made");
    }

    @Test
    @DisplayName("E: submit with an invalid submission throws ValidationException")
    void E_submitInvalidThrowsValidationException() {
        Submission invalid = new Submission("12", "1.1P", "body", FileType.PDF, Instant.now());

        ValidationException ex = assertThrows(ValidationException.class, () -> service.submit(invalid));

        assertTrue(ex.errors().stream().anyMatch(e -> e.toLowerCase().contains("studentid")),
                "exception must carry the studentId error");
    }

    @Test
    @DisplayName("P: submit 10000 then inbox lookup completes under 500ms")
    void P_submit10kAndLookupUnder500ms() {
        for (int i = 0; i < 10_000; i++) {
            service.submit(validSubmission("225053119"));
        }

        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            List<SubmissionRecord> inbox = service.inbox("225053119");
            assertEquals(10_000, inbox.size(), "all 10000 submissions must be returned");
        });
    }
}
