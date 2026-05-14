package com.onTrack;

import java.util.Collections;
import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super(buildMessage(errors));
        this.errors = List.copyOf(errors);
    }

    public List<String> errors() {
        return Collections.unmodifiableList(errors);
    }

    private static String buildMessage(List<String> errors) {
        if (errors == null || errors.isEmpty()) {
            return "validation failed";
        }
        return "validation failed: " + String.join("; ", errors);
    }
}
