import org.example.simplyfility.tests.TestFerma;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestFermaTests {
    private final TestFerma testFerma = new TestFerma();


    @ParameterizedTest
    @MethodSource("provideTestCases")
    void testIsSimple(BigInteger number, double chance, boolean expected) {
        assertEquals(expected, testFerma.isSimple(number, chance));
    }

    @Test
    void testChanceBoundaries() {
        assertDoesNotThrow(() -> testFerma.isSimple(BigInteger.valueOf(17), 0.5));
        assertDoesNotThrow(() -> testFerma.isSimple(BigInteger.valueOf(17), 0.999));
        assertThrows(IllegalArgumentException.class,
                () -> testFerma.isSimple(BigInteger.valueOf(17), 0.4));
        assertThrows(IllegalArgumentException.class,
                () -> testFerma.isSimple(BigInteger.valueOf(17), 1.0));
    }


    @Test
    void testKnownPrimes() {
        assertTrue(testFerma.isSimple(BigInteger.valueOf(17), 0.99));
        assertTrue(testFerma.isSimple(BigInteger.valueOf(7919), 0.99));
        assertTrue(testFerma.isSimple(BigInteger.valueOf(1000003), 0.99));
    }


    @Test
    void testKnownComposites() {
        assertFalse(testFerma.isSimple(BigInteger.valueOf(15), 0.99));
        assertFalse(testFerma.isSimple(BigInteger.valueOf(1000001), 0.99));
    }


    @Test
    void testCarmichaelNumbers() {
        assertFalse(testFerma.isSimple(BigInteger.valueOf(561), 0.99));
        assertFalse(testFerma.isSimple(BigInteger.valueOf(1105), 0.99));
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(BigInteger.valueOf(2), 0.9, true),
                Arguments.of(BigInteger.valueOf(3), 0.9, true),
                Arguments.of(BigInteger.valueOf(4), 0.9, false),
                Arguments.of(BigInteger.valueOf(97), 0.99, true),
                Arguments.of(BigInteger.valueOf(100), 0.99, false)
        );
    }
}
