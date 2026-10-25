package com.bms.triangle.core;

import com.bms.triangle.core.model.Triangle;
import com.bms.triangle.core.enums.TriangleType;
import java.math.BigInteger;
import java.util.List;

/** Validates three side lengths given as text and classifies the triangle they form. */
public final class TriangleClassifier {

    private final ValidationService validationService = new ValidationService();

    /**
     * @throws InvalidSidesException if any side is not a valid positive integer; lists every invalid side
     * @throws NotATriangleException if all sides are valid but break the strict triangle inequality
     */
    public TriangleType classify(String a, String b, String c) {
        List<BigInteger> sides = validationService.validate(a, b, c);
        
        return new Triangle(sides.get(0), sides.get(1), sides.get(2)).type();
    }
}
