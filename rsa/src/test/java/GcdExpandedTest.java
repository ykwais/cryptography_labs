import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.example.stateless.Math.gcdExpansion;
import static org.junit.jupiter.api.Assertions.*;

class GcdExpandedTest {
    @Test
    void testBasicCase() {
        BigInteger[] result = gcdExpansion(
                new BigInteger("48"),
                new BigInteger("18")
        );
        assertArrayEquals(
                new BigInteger[]{new BigInteger("6"), new BigInteger("-1"), new BigInteger("3")},
                result
        );
        // Проверка: 48*(-1) + 18*3 = 6
        assertEquals(
                result[0],
                new BigInteger("48").multiply(result[1])
                        .add(new BigInteger("18").multiply(result[2]))
        );
    }

    @Test
    void testPrimeNumbers() {
        BigInteger[] result = gcdExpansion(
                new BigInteger("17"),
                new BigInteger("5")
        );
        assertArrayEquals(
                new BigInteger[]{new BigInteger("1"), new BigInteger("-2"), new BigInteger("7")},
                result
        );
        // 17*(-2) + 5*7 = 1
        assertEquals(
                BigInteger.ONE,
                new BigInteger("17").multiply(result[1])
                        .add(new BigInteger("5").multiply(result[2]))
        );
    }

    @Test
    void testZeroCase() {
        BigInteger[] result = gcdExpansion(
                BigInteger.ZERO,
                new BigInteger("15")
        );
        assertArrayEquals(
                new BigInteger[]{new BigInteger("15"), BigInteger.ZERO, BigInteger.ONE},
                result
        );
    }

    @Test
    void testEqualNumbers() {
        BigInteger[] result = gcdExpansion(
                new BigInteger("25"),
                new BigInteger("25")
        );
        assertArrayEquals(
                new BigInteger[]{new BigInteger("25"), BigInteger.ONE, BigInteger.ZERO},
                result
        );
    }

    @Test
    void testLargeNumbers() {
        BigInteger a = new BigInteger("12345678901234567890");
        BigInteger b = new BigInteger("9876543210");
        BigInteger[] result = gcdExpansion(a, b);

        assertEquals(
                result[0],
                a.multiply(result[1]).add(b.multiply(result[2]))
        );
    }
}
