package com.bms.triangle.core.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bms.triangle.core.enums.TriangleType;
import com.bms.triangle.core.exception.NotATriangleException;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TriangleTest {

    private static final BigInteger LARGE = new BigInteger("99999999999999999999");

    static Stream<Arguments> classifiedTriangles() {
        return Stream.of(
                withType(TriangleType.EQUILATERAL, side(3), side(3), side(3)),
                withType(TriangleType.ISOSCELES, side(3), side(3), side(5)),
                withType(TriangleType.SCALENE, side(3), side(4), side(5)),
                withType(TriangleType.ISOSCELES, LARGE, LARGE, side(1)))
                .flatMap(cases -> cases);
    }

    @ParameterizedTest(name = "[{index}] {0} -> {1}")
    @MethodSource("classifiedTriangles")
    void typeDoesNotDependOnSideOrder(List<BigInteger> sides, TriangleType expected) {
        assertThat(triangle(sides).type()).isEqualTo(expected);
    }

    static Stream<List<BigInteger>> nonTriangles() {
        return Stream.of(
                permutations(side(1), side(2), side(3)),    // degenerate: 1 + 2 == 3
                permutations(side(1), side(1), side(10)))   // one side too long
                .flatMap(List::stream);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("nonTriangles")
    void rejectsSidesThatBreakTheStrictTriangleInequality(List<BigInteger> sides) {
        assertThatThrownBy(() -> triangle(sides)).isInstanceOf(NotATriangleException.class);
    }

    private static Stream<Arguments> withType(TriangleType type, BigInteger a, BigInteger b, BigInteger c) {
        return permutations(a, b, c).stream().map(sides -> Arguments.of(sides, type));
    }

    private static BigInteger side(long length) {
        return BigInteger.valueOf(length);
    }

    private static Triangle triangle(List<BigInteger> sides) {
        return new Triangle(sides.get(0), sides.get(1), sides.get(2));
    }

    private static List<List<BigInteger>> permutations(BigInteger a, BigInteger b, BigInteger c) {
        return List.of(
                List.of(a, b, c), List.of(a, c, b),
                List.of(b, a, c), List.of(b, c, a),
                List.of(c, a, b), List.of(c, b, a));
    }
}
