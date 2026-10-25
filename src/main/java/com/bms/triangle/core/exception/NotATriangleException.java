package com.bms.triangle.core.exception;

import java.math.BigInteger;
import java.util.List;

public final class NotATriangleException extends RuntimeException {

    private final List<BigInteger> sides;

    public NotATriangleException(BigInteger a, BigInteger b, BigInteger c) {
        super("Sides %s, %s, %s do not form a triangle".formatted(a, b, c));
        this.sides = List.of(a, b, c);
    }

    /** The three sides, in the order given. */
    public List<BigInteger> sides() {
        return sides;
    }
}
