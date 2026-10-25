package com.bms.triangle.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import com.bms.triangle.core.exception.InvalidSidesException;
import com.bms.triangle.core.exception.NotATriangleException;
import com.bms.triangle.core.enums.TriangleType;
import com.bms.triangle.core.model.ValidationError;
import com.bms.triangle.core.enums.ValidationReason;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TriangleClassifierTest {

    private final TriangleClassifier classifier = new TriangleClassifier();

    @ParameterizedTest(name = "[{index}] \"{0}\" \"{1}\" \"{2}\" -> {3}")
    @CsvSource(delimiter = '|', textBlock = """
            3                    | 3                    | 3   | EQUILATERAL
            3                    | 3                    | 5   | ISOSCELES
            5                    | 3                    | 3   | ISOSCELES
            3                    | 4                    | 5   | SCALENE
            99999999999999999999 | 99999999999999999999 | 1   | ISOSCELES
            """)
    void classifiesValidSides(String a, String b, String c, TriangleType expected) {
        assertThat(classifier.classify(a, b, c)).isEqualTo(expected);
    }

    @Test
    void reportsEveryInvalidSideFromValidation() {
        assertThat(validationErrors("abc", "-1", "0")).containsExactly(
                new ValidationError("abc", ValidationReason.NOT_AN_INTEGER),
                new ValidationError("-1", ValidationReason.NOT_POSITIVE),
                new ValidationError("0", ValidationReason.NOT_POSITIVE));
    }

    @ParameterizedTest(name = "[{index}] {0} {1} {2}")
    @CsvSource({
        "1,   1, 1.0",   // would be equilateral, but side 3 is invalid
        "0,   1, 10",    // would also break the triangle inequality
    })
    void skipsTheTriangleCheckWhenAnySideIsInvalid(String a, String b, String c) {
        assertThat(validationErrors(a, b, c)).hasSize(1);
    }

    @Test
    void rejectsSidesThatDoNotFormATriangle() {
        assertThatThrownBy(() -> classifier.classify("1", "10", "1"))
                .isInstanceOf(NotATriangleException.class);
    }

    private List<ValidationError> validationErrors(String a, String b, String c) {
        InvalidSidesException exception =
                catchThrowableOfType(InvalidSidesException.class, () -> classifier.classify(a, b, c));
        assertThat(exception).as("expected InvalidSidesException").isNotNull();
        return exception.errors();
    }
}
