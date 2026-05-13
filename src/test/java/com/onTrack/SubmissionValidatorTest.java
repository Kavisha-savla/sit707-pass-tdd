package com.onTrack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubmissionValidatorTest {

    private final SubmissionValidator validator = new SubmissionValidator();

    @Test
    @DisplayName("Cycle 1 RED: a null submission is rejected with a clear error")
    void nullSubmissionIsRejected() {
        ValidationResult result = validator.validate(null);

        assertNotNull(result, "validator must never return null");
        assertFalse(result.isValid(), "null submission must be invalid");
        assertTrue(
                result.errors().stream().anyMatch(e -> e.toLowerCase().contains("submission")),
                "errors should mention the submission itself"
        );
    }
}
