package com.bms.triangle.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import com.bms.triangle.core.exception.InvalidSidesException;
import com.bms.triangle.core.model.ValidationError;
import com.bms.triangle.core.enums.ValidationReason;
import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class TriangleValidatorTest {

    private final TriangleValidator triangleValidator = new TriangleValidator();

    @ParameterizedTest(name = "[{index}] \"{0}\" -> {1}")
    @CsvSource(delimiter = '|', textBlock = """
            5                    | 5
            99999999999999999999 | 99999999999999999999
            """)
    void returnsValidSidesAsNumbers(String value, BigInteger expected) {
        assertThat(triangleValidator.validate(value, "4", "5"))
                .containsExactly(expected, BigInteger.valueOf(4), BigInteger.valueOf(5));
    }

    @ParameterizedTest(name = "[{index}] \"{0}\"")
    @ValueSource(strings = {"abc", "x", "4.0"})
    void rejectsValuesThatAreNotValidIntegers(String value) {
        assertThat(validationErrors(value, "4", "5"))
                .containsExactly(new ValidationError(value, ValidationReason.NOT_AN_INTEGER));
    }

    @ParameterizedTest(name = "[{index}] \"{0}\"")
    @ValueSource(strings = {"0", "-1", "-99999999999999999999"})
    void rejectsValuesThatAreNotPositive(String value) {
        assertThat(validationErrors(value, "4", "5"))
                .containsExactly(new ValidationError(value, ValidationReason.NOT_POSITIVE));
    }

    @Test
    void reportsEveryInvalidSideInArgumentOrder() {
        assertThat(validationErrors("abc", "-1", "0")).containsExactly(
                new ValidationError("abc", ValidationReason.NOT_AN_INTEGER),
                new ValidationError("-1", ValidationReason.NOT_POSITIVE),
                new ValidationError("0", ValidationReason.NOT_POSITIVE));
    }

    @Test
    void doesNotCheckTheTriangleInequality() {
        assertThat(triangleValidator.validate("1", "2", "3"))
                .containsExactly(BigInteger.ONE, BigInteger.TWO, BigInteger.valueOf(3));
    }

    private List<ValidationError> validationErrors(String a, String b, String c) {
        InvalidSidesException exception =
                catchThrowableOfType(InvalidSidesException.class, () -> triangleValidator.validate(a, b, c));
        assertThat(exception).as("expected InvalidSidesException").isNotNull();
        return exception.errors();
    }
}
