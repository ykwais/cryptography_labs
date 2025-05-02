import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.example.stateless.Math.powMod;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PowModTest {

    @Test
    void testPositiveNumbers() {
        BigInteger a = new BigInteger("3");
        BigInteger n = new BigInteger("4");
        BigInteger m = new BigInteger("5");
        assertEquals(BigInteger.ONE, powMod(a, n, m));  // 3^4 mod 5 = 81 mod 5 = 1
    }

    @Test
    void testNegativeBase() {
        BigInteger a = new BigInteger("-2");
        BigInteger n = new BigInteger("3");
        BigInteger m = new BigInteger("10");
        assertEquals(new BigInteger("2"), powMod(a, n, m));  // (-2)^3 mod 10 = -8 mod 10 = 2
    }

    @Test
    void testZeroExponent() {
        BigInteger a = new BigInteger("5");
        BigInteger n = BigInteger.ZERO;
        BigInteger m = new BigInteger("100");
        assertEquals(BigInteger.ONE, powMod(a, n, m));  // 5^0 mod 100 = 1
    }

    @Test
    void testModOne() {
        BigInteger a = new BigInteger("123456789");
        BigInteger n = new BigInteger("1000");
        BigInteger m = BigInteger.ONE;
        assertEquals(BigInteger.ZERO, powMod(a, n, m));  // Любое число mod 1 = 0
    }

    @Test
    void testLargeNumbers() {
        BigInteger a = new BigInteger("123456789");
        BigInteger n = new BigInteger("1000000");
        BigInteger m = new BigInteger("1000000007");
        BigInteger expected = new BigInteger("471040903");
        assertEquals(expected, powMod(a, n, m));
    }

    @Test
    void testModZeroThrowsException() {
        BigInteger a = BigInteger.TEN;
        BigInteger n = BigInteger.TWO;
        BigInteger m = BigInteger.ZERO;
        assertThrows(IllegalArgumentException.class, () -> powMod(a, n, m));
    }
}
