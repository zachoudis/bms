package com.bms.triangle.core;

import com.bms.triangle.core.exception.InvalidSidesException;
import com.bms.triangle.core.model.ValidationError;
import com.bms.triangle.core.enums.ValidationReason;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Validates three side lengths given as text and converts them to numbers. */
public final class TriangleValidator {

    private static final Pattern INTEGER = Pattern.compile("-?[0-9]+");

    /**
     * @return the three sides as numbers, in the order given
     * @throws InvalidSidesException if any side is not a valid positive integer; lists every invalid side
     */
    public List<BigInteger> validate(String a, String b, String c) {
        List<BigInteger> sides = new ArrayList<>();
        List<ValidationError> errors = new ArrayList<>();

        for (String value : List.of(a, b, c)) {
            if (!INTEGER.matcher(value).matches()) {
                errors.add(new ValidationError(value, ValidationReason.NOT_AN_INTEGER));
            } else if (new BigInteger(value).signum() <= 0) {
                errors.add(new ValidationError(value, ValidationReason.NOT_POSITIVE));
            } else {
                sides.add(new BigInteger(value));
            }
        }

        if (!errors.isEmpty()) {
            throw new InvalidSidesException(errors);
        }
        return sides;
    }
}
