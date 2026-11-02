package com.bms.triangle.core;

import com.bms.triangle.core.enums.TriangleType;
import com.bms.triangle.core.exception.InvalidSidesException;
import com.bms.triangle.core.exception.NotATriangleException;
import com.bms.triangle.core.model.Triangle;
import java.math.BigInteger;
import java.util.List;

/** Classifies the triangle formed by three side lengths given as text, after {@link TriangleValidator} checks them. */
public final class TriangleClassifier {

    private final TriangleValidator triangleValidator = new TriangleValidator();

    /**
     * @throws InvalidSidesException if any side is not a valid positive integer; lists every invalid side
     * @throws NotATriangleException if all sides are valid but break the strict triangle inequality
     */
    public TriangleType classify(String a, String b, String c) {
        List<BigInteger> sides = triangleValidator.validate(a, b, c);
        
        return new Triangle(sides.get(0), sides.get(1), sides.get(2)).type();
    }
}
