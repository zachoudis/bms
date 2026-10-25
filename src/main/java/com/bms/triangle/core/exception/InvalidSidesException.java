package com.bms.triangle.core.exception;

import com.bms.triangle.core.model.ValidationError;
import java.util.List;

public final class InvalidSidesException extends RuntimeException {

    private final List<ValidationError> errors;

    public InvalidSidesException(List<ValidationError> errors) {
        super("Invalid sides: " + errors);
        this.errors = List.copyOf(errors);
    }

    /** Every invalid side, in argument order. */
    public List<ValidationError> errors() {
        return errors;
    }
}
