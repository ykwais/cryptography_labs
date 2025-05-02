import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.Stream;

import static org.example.stateless.Math.jacobi;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JacobiTest {
    @ParameterizedTest
    @MethodSource("provideValidInputs")
    void testValidInputs(BigInteger a, BigInteger n, int expected) {
        assertEquals(expected, jacobi(a, n));
    }

    @Test
    void testZeroNumerator() {
        assertEquals(0, jacobi(BigInteger.ZERO, new BigInteger("15")));
    }

    @Test
    void testEvenModulusThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                jacobi(BigInteger.ONE, BigInteger.TWO)
        );
    }

    @Test
    void testSmallModulusThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                jacobi(BigInteger.ONE, BigInteger.ONE)
        );
    }

    @Test
    void testLargeNumbers() {
        BigInteger bigPrime = new BigInteger("1000000000000000003");
        assertEquals(-1, jacobi(new BigInteger("2"), bigPrime));
        assertEquals(-1, jacobi(new BigInteger("3"), bigPrime));
    }

    @Test
    void testNegativeNumerator() {
        assertEquals(1, jacobi(new BigInteger("-1"), new BigInteger("5")));
        assertEquals(-1, jacobi(new BigInteger("-2"), new BigInteger("7")));
    }

    private static Stream<Arguments> provideValidInputs() {
        return Stream.of(
                // (a, n, expected)
                Arguments.of(new BigInteger("1"), new BigInteger("3"), 1),
                Arguments.of(new BigInteger("2"), new BigInteger("3"), -1),
                Arguments.of(new BigInteger("3"), new BigInteger("5"), -1),
                Arguments.of(new BigInteger("4"), new BigInteger("5"), 1),
                Arguments.of(new BigInteger("5"), new BigInteger("9"), 1),
                Arguments.of(new BigInteger("6"), new BigInteger("15"), 0),
                Arguments.of(new BigInteger("7"), new BigInteger("15"), -1),
                Arguments.of(new BigInteger("8"), new BigInteger("15"), 1),
                Arguments.of(new BigInteger("9"), new BigInteger("15"), 0)
        );
    }

}
