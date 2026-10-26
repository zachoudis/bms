package com.bms.triangle.cli.enums;

public enum ExitCode {
    SUCCESS(0),
    INVALID_TRIANGLE(1),
    USAGE_ERROR(2);

    private final int code;

    ExitCode(int code) {
        this.code = code;
    }

    /** The process exit code. */
    public int code() {
        return code;
    }
}
