package com.github.cybellereaper.scratchy.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ValidationResult {
    private final List<String> errors;

    private ValidationResult(List<String> errors) {
        this.errors = List.copyOf(errors);
    }

    public static ValidationResult ok() {
        return new ValidationResult(List.of());
    }

    public static ValidationResult fromErrors(List<String> errors) {
        return new ValidationResult(new ArrayList<>(errors));
    }

    public boolean valid() {
        return errors.isEmpty();
    }

    public List<String> errors() {
        return Collections.unmodifiableList(errors);
    }
}
