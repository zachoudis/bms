package com.bms.triangle.core.model;

import com.bms.triangle.core.enums.TriangleType;
import com.bms.triangle.core.exception.NotATriangleException;
import java.math.BigInteger;
import java.util.Objects;

/** Three side lengths that satisfy the strict triangle inequality. */
public record Triangle(BigInteger a, BigInteger b, BigInteger c) {

    /** @throws NotATriangleException if any two sides do not add up to more than the third */
    public Triangle {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        Objects.requireNonNull(c, "c");
        if (!(isLonger(a.add(b), c) && isLonger(a.add(c), b) && isLonger(b.add(c), a))) {
            throw new NotATriangleException(a, b, c);
        }
    }

    public TriangleType type() {
        if (a.equals(b) && b.equals(c)) {
            return TriangleType.EQUILATERAL;
        }
        if (a.equals(b) || b.equals(c) || a.equals(c)) {
            return TriangleType.ISOSCELES;
        }
        return TriangleType.SCALENE;
    }

    private static boolean isLonger(BigInteger length, BigInteger other) {
        return length.compareTo(other) > 0;
    }
}
