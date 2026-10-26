package com.bms.triangle.cli.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ExitCodeTest {

    @Test
    void codesMatchTheSpec() {
        assertThat(ExitCode.SUCCESS.code()).isEqualTo(0);
        assertThat(ExitCode.INVALID_TRIANGLE.code()).isEqualTo(1);
        assertThat(ExitCode.USAGE_ERROR.code()).isEqualTo(2);
    }
}
