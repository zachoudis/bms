package com.bms.triangle.cli;

import com.bms.triangle.cli.enums.ExitCode;
import com.bms.triangle.core.TriangleClassifier;
import com.bms.triangle.core.exception.InvalidSidesException;
import com.bms.triangle.core.exception.NotATriangleException;
import com.bms.triangle.core.enums.TriangleType;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.List;

/** Turns command-line arguments into output and an exit code. All triangle logic lives in the core package. */
public final class TriangleClassifierCommand {

    private static final String USAGE = "Usage: triangle-classifier <a> <b> <c>";
    private static final TriangleClassifier classifier = new TriangleClassifier();

    private TriangleClassifierCommand() {
    }

    public static ExitCode run(String[] args, PrintStream out, PrintStream err) {
        if (args.length != 3) {
            printLine(err, USAGE);
            return ExitCode.USAGE_ERROR;
        }
        try {
            TriangleType type = classifier.classify(args[0], args[1], args[2]);
            printLine(out, type.displayName());
            return ExitCode.SUCCESS;
        } catch (InvalidSidesException exception) {
            exception.errors().forEach(error ->
                    printLine(err, "Error: \"%s\" %s".formatted(error.value(), error.reason().problem())));
            return ExitCode.INVALID_TRIANGLE;
        } catch (NotATriangleException exception) {
            List<BigInteger> sides = exception.sides();
            printLine(err, "Error: sides %s, %s, %s do not form a triangle"
                    .formatted(sides.get(0), sides.get(1), sides.get(2)));
            return ExitCode.INVALID_TRIANGLE;
        }
    }

    private static void printLine(PrintStream stream, String line) {
        stream.print(line + "\n");
        stream.flush();
    }
}
