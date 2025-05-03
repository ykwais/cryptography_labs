import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.Stream;


import static org.example.stateless.Math.gcd;
import static org.junit.jupiter.api.Assertions.*;

class GcdDefaultTest {
    @Test
    void testGcdWithZero() {
        assertEquals(BigInteger.TEN, gcd(BigInteger.TEN, BigInteger.ZERO));
        assertEquals(BigInteger.TEN, gcd(BigInteger.ZERO, BigInteger.TEN));
    }

    @Test
    void testGcdWithOne() {
        assertEquals(BigInteger.ONE, gcd(BigInteger.ONE, BigInteger.TEN));
        assertEquals(BigInteger.ONE, gcd(BigInteger.valueOf(17), BigInteger.ONE));
    }

    @ParameterizedTest
    @MethodSource("provideNumbersForGcd")
    void testGcdWithVariousNumbers(BigInteger a, BigInteger b, BigInteger expected) {
        assertEquals(expected, gcd(a, b));
        assertEquals(expected, gcd(b, a));
    }

    @Test
    void testGcdWithLargePrimes() {
        BigInteger prime1 = new BigInteger("32416190071");
        BigInteger prime2 = new BigInteger("2305843009213693951");
        assertEquals(BigInteger.ONE, gcd(prime1, prime2));
    }

    @Test
    void testNegative() {
        assertEquals(new BigInteger("5"), gcd(new BigInteger("-15"), new BigInteger("20")));
    }

    @Test
    void testGcdWithNegativeNumbers() {
        assertEquals(BigInteger.valueOf(4), gcd(BigInteger.valueOf(-12), BigInteger.valueOf(8)));
        assertEquals(BigInteger.valueOf(3), gcd(BigInteger.valueOf(9), BigInteger.valueOf(-6)));
    }

    @Test
    void testGcdPerformance() {
        BigInteger veryLarge1 = new BigInteger("123456789012345678901234567890");
        BigInteger veryLarge2 = new BigInteger("987654321098765432109876543210");

        BigInteger result = gcd(veryLarge1, veryLarge2);

        assertEquals(new BigInteger("9000000000900000000090"), result);
    }

    private static Stream<Arguments> provideNumbersForGcd() {
        return Stream.of(
                Arguments.of(BigInteger.valueOf(48), BigInteger.valueOf(18), BigInteger.valueOf(6)),
                Arguments.of(BigInteger.valueOf(17), BigInteger.valueOf(5), BigInteger.valueOf(1)),
                Arguments.of(BigInteger.valueOf(60), BigInteger.valueOf(48), BigInteger.valueOf(12)),
                Arguments.of(BigInteger.valueOf(1071), BigInteger.valueOf(462), BigInteger.valueOf(21)),
                Arguments.of(BigInteger.valueOf(10), BigInteger.valueOf(10), BigInteger.valueOf(10))
        );
    }

    @Test
    void testGcdWithZeroAndZero() {
        assertThrows(IllegalArgumentException.class, () -> gcd(BigInteger.ZERO, BigInteger.ZERO));
    }

    @Test
    void testGcdWithSameNumbers() {
        BigInteger num = new BigInteger("123456789");
        assertEquals(num, gcd(num, num));
    }

    @Test
    void testGcdWithFibonacciNumbers() {
        BigInteger fib20 = BigInteger.valueOf(6765);
        BigInteger fib15 = BigInteger.valueOf(610);
        assertEquals(BigInteger.valueOf(5), gcd(fib20, fib15));
    }

    @Test
    void testGcdWithNullFirstArgument() {
        assertThrows(IllegalArgumentException.class, () -> gcd(null, BigInteger.ONE));
    }

    @Test
    void testGcdWithNullSecondArgument() {
        assertThrows(IllegalArgumentException.class, () -> gcd(BigInteger.ONE, null));
    }

    @Test
    void testGcdAndLcmRelationship() {
        BigInteger a = new BigInteger("123456789");
        BigInteger b = new BigInteger("987654321");
        BigInteger gcdResult = gcd(a, b);
        BigInteger lcm = a.multiply(b).divide(gcdResult);

        assertEquals(0, a.remainder(gcdResult).intValue());
        assertEquals(0, b.remainder(gcdResult).intValue());
        assertEquals(0, lcm.remainder(a).intValue());
        assertEquals(0, lcm.remainder(b).intValue());
        assertEquals(a.multiply(b), gcdResult.multiply(lcm));
    }
}
