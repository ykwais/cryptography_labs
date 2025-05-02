import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.example.stateless.Math.legendre;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LegendreTest {
    @Test
    void quadraticResidue() {
        // 5 ≡ 4² mod 11 → квадратичный вычет
        assertEquals(1, legendre(
                new BigInteger("5"),
                new BigInteger("11")
        ));

        // 2 ≡ 3² mod 7 → квадратичный вычет
        assertEquals(1, legendre(
                new BigInteger("2"),
                new BigInteger("7")
        ));
    }

    @Test
    void quadraticNonResidue() {
        // 3 не является квадратом mod 7
        assertEquals(-1, legendre(
                new BigInteger("3"),
                new BigInteger("7")
        ));

        // 2 не является квадратом mod 5
        assertEquals(-1, legendre(
                new BigInteger("2"),
                new BigInteger("5")
        ));
    }

    @Test
    void zeroCase() {
        // 15 ≡ 0 mod 5
        assertEquals(0, legendre(
                new BigInteger("15"),
                new BigInteger("5")
        ));

        // 0 ≡ 0 mod p для любого p
        assertEquals(0, legendre(
                BigInteger.ZERO,
                new BigInteger("13")
        ));
    }

    @Test
    void specialCases() {
        // (-1/11) = -1 (так как 11 ≡ 3 mod 4)
        assertEquals(-1, legendre(
                new BigInteger("-1"),
                new BigInteger("11")
        ));

        // (2/7) = 1 (так как 7 ≡ 7 mod 8 → по второму доп. закону)
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
        // 1234567890 и простое 1000000007
        assertEquals(-1, legendre(
                new BigInteger("1234567890"),
                new BigInteger("1000000007")
        ));

        // 987654321 и простое 1000000007 → невычет
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
        // p не простое → исключение
        assertThrows(IllegalArgumentException.class, () -> {
            legendre(
                    new BigInteger("5"),
                    new BigInteger("10") // 10 — не простое
            );
        });

        // p = 2 → исключение (требуется нечётное простое)
        assertThrows(IllegalArgumentException.class, () -> {
            legendre(
                    new BigInteger("3"),
                    BigInteger.TWO
            );
        });
    }
}
