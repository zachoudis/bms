package com.bms.triangle.core.enums;

public enum ValidationReason {
    NOT_AN_INTEGER("is not a valid integer"),
    NOT_POSITIVE("must be positive");

    private final String problem;

    ValidationReason(String problem) {
        this.problem = problem;
    }

    /** What is wrong with the side, worded to follow its quoted value. */
    public String problem() {
        return problem;
    }
}
