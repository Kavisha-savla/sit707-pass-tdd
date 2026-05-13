package com.onTrack;

import java.util.Collections;
import java.util.List;

public final class ValidationResult {

    private final boolean valid;
    private final List<String> errors;

    private ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = Collections.unmodifiableList(errors);
    }

    public static ValidationResult ok() {
        return new ValidationResult(true, List.of());
    }

    public static ValidationResult failed(List<String> errors) {
        if (errors == null || errors.isEmpty()) {
            throw new IllegalArgumentException("failed() requires at least one error");
        }
        return new ValidationResult(false, errors);
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> errors() {
        return errors;
    }
}
