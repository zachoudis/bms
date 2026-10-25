package com.bms.triangle.core.exception;

import java.math.BigInteger;

public final class NotATriangleException extends RuntimeException {

    public NotATriangleException(BigInteger a, BigInteger b, BigInteger c) {
        super("Not a triangle: %s, %s, %s".formatted(a, b, c));
    }
}
