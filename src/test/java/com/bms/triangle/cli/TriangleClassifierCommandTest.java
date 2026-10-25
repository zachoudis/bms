package com.bms.triangle.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TriangleClassifierCommandTest {

    private static final String USAGE = "Usage: triangle-classifier <a> <b> <c>\n";

    /** The SPEC.md Examples table, row by row, followed by a few extra cases. */
    static Stream<Arguments> examples() {
        return Stream.of(
                success(args("3", "3", "3"), "Equilateral\n"),
                success(args("3", "3", "5"), "Isosceles\n"),
                success(args("5", "3", "3"), "Isosceles\n"),
                success(args("3", "4", "5"), "Scalene\n"),
                success(args("99999999999999999999", "99999999999999999999", "1"), "Isosceles\n"),
                failure(args("3", "4"), ExitCode.USAGE_ERROR, USAGE),
                failure(args("3", "4", "5", "6"), ExitCode.USAGE_ERROR, USAGE),
                failure(args("abc", "4", "5"), ExitCode.INVALID_TRIANGLE,
                        "Error: \"abc\" is not a valid integer\n"),
                failure(args("3", "4.0", "5"), ExitCode.INVALID_TRIANGLE,
                        "Error: \"4.0\" is not a valid integer\n"),
                failure(args("0", "4", "5"), ExitCode.INVALID_TRIANGLE,
                        "Error: \"0\" must be positive\n"),
                failure(args("abc", "-1", "0"), ExitCode.INVALID_TRIANGLE, """
                        Error: "abc" is not a valid integer
                        Error: "-1" must be positive
                        Error: "0" must be positive
                        """),
                failure(args("1", "2", "3"), ExitCode.INVALID_TRIANGLE,
                        "Error: sides 1, 2, 3 do not form a triangle\n"),
                failure(args("1", "1", "10"), ExitCode.INVALID_TRIANGLE,
                        "Error: sides 1, 1, 10 do not form a triangle\n"),

                // Extra cases from the spec rules
                failure(args("1", "10", "1"), ExitCode.INVALID_TRIANGLE,
                        "Error: sides 1, 10, 1 do not form a triangle\n"),   // sides in argument order
                failure(args(), ExitCode.USAGE_ERROR, USAGE),
                failure(args("--help"), ExitCode.USAGE_ERROR, USAGE));
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("examples")
    void writesExpectedOutputAndExitCode(String description, String[] args, Result expected) {
        assertThat(run(args)).isEqualTo(expected);
    }

    record Result(ExitCode exitCode, String stdout, String stderr) {
    }

    private static Arguments success(String[] args, String stdout) {
        return Arguments.of(describe(args), args, new Result(ExitCode.SUCCESS, stdout, ""));
    }

    private static Arguments failure(String[] args, ExitCode exitCode, String stderr) {
        return Arguments.of(describe(args), args, new Result(exitCode, "", stderr));
    }

    private static String[] args(String... args) {
        return args;
    }

    private static String describe(String[] args) {
        return args.length == 0 ? "(no arguments)" : '"' + String.join("\" \"", args) + '"';
    }

    private static Result run(String... args) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        ExitCode exitCode = TriangleClassifierCommand.run(args,
                new PrintStream(out, true, StandardCharsets.UTF_8),
                new PrintStream(err, true, StandardCharsets.UTF_8));
        return new Result(exitCode, out.toString(StandardCharsets.UTF_8), err.toString(StandardCharsets.UTF_8));
    }
}
