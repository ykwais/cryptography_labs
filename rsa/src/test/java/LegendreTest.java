import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.example.stateless.Math.legendre;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LegendreTest {
    @Test
    void quadraticResidue() {

        assertEquals(1, legendre(
                new BigInteger("5"),
                new BigInteger("11")
        ));

        assertEquals(1, legendre(
                new BigInteger("2"),
                new BigInteger("7")
        ));
    }

    @Test
    void quadraticNonResidue() {
        assertEquals(-1, legendre(
                new BigInteger("3"),
                new BigInteger("7")
        ));

        assertEquals(-1, legendre(
                new BigInteger("2"),
                new BigInteger("5")
        ));
    }

    @Test
    void zeroCase() {
        assertEquals(0, legendre(
                new BigInteger("15"),
                new BigInteger("5")
        ));

        assertEquals(0, legendre(
                BigInteger.ZERO,
                new BigInteger("13")
        ));
    }

    @Test
    void specialCases() {
        assertEquals(-1, legendre(
                new BigInteger("-1"),
                new BigInteger("11")
        ));

        assertEquals(1, legendre(
                new BigInteger("2"),
                new BigInteger("7")
        ));

        assertEquals(1, legendre(
                new BigInteger("-1"),
                new BigInteger("17")
        ));

        assertEquals(-1, legendre(
                new BigInteger("56"),
                new BigInteger("65537")
        ));
    }

    @Test
    void largeNumbers() {
        assertEquals(-1, legendre(
                new BigInteger("1234567890"),
                new BigInteger("1000000007")
        ));

        assertEquals(1, legendre(
                new BigInteger("987654321"),
                new BigInteger("1000000007")
        ));

        assertEquals(-1, legendre(
                new BigInteger("4698450"),
                new BigInteger("1000000007")
        ));


    }

    @Test
    void invalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            legendre(
                    new BigInteger("5"),
                    new BigInteger("10")
            );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            legendre(
                    new BigInteger("3"),
                    BigInteger.TWO
            );
        });
    }
}
