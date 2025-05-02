import org.example.simplyfility.tests.MillerRabinTest;
import org.example.simplyfility.tests.SolovayStrassenTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestsMillerRabbinTest {
    private final MillerRabinTest test = new MillerRabinTest();


    @ParameterizedTest
    @MethodSource("provideTestCases")
    void testIsSimple(BigInteger number, double chance, boolean expected) {
        assertEquals(expected, test.isSimple(number, chance));
    }

    @Test
    void testChanceBoundaries() {
        assertDoesNotThrow(() -> test.isSimple(BigInteger.valueOf(17), 0.5));
        assertDoesNotThrow(() -> test.isSimple(BigInteger.valueOf(17), 0.999));
        assertThrows(IllegalArgumentException.class,
                () -> test.isSimple(BigInteger.valueOf(17), 0.4));
        assertThrows(IllegalArgumentException.class,
                () -> test.isSimple(BigInteger.valueOf(17), 1.0));
    }


    @Test
    void testKnownPrimes() {
        assertTrue(test.isSimple(BigInteger.valueOf(17), 0.99));
        assertTrue(test.isSimple(BigInteger.valueOf(7919), 0.99)); // 1000-е простое
        assertTrue(test.isSimple(BigInteger.valueOf(1000003), 0.99));
    }


    @Test
    void testKnownComposites() {
        assertFalse(test.isSimple(BigInteger.valueOf(15), 0.99));
        assertFalse(test.isSimple(BigInteger.valueOf(1000001), 0.99)); // 1007*993
    }


    @Test
    void testCarmichaelNumbers() {
        assertFalse(test.isSimple(BigInteger.valueOf(561), 0.99));//Кармайкл
        assertFalse(test.isSimple(BigInteger.valueOf(1105), 0.99));
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
