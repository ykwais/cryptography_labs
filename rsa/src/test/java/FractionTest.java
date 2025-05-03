import javafx.util.Pair;
import org.example.attacks.AttackWiener;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FractionTest {
    @Test
    void testGetFractionSimpleRatio() {

        List<BigInteger> result = AttackWiener.getFraction(
                BigInteger.valueOf(5),
                BigInteger.valueOf(3)
        );
        assertEquals(List.of(
                BigInteger.ONE,
                BigInteger.ONE,
                BigInteger.valueOf(2)
        ), result);
    }

    @Test
    void testGetFractionGoldenRatio() {
        List<BigInteger> result = AttackWiener.getFraction(
                BigInteger.valueOf(89),
                BigInteger.valueOf(55)
        );
        assertEquals(List.of(
                BigInteger.ONE, BigInteger.ONE, BigInteger.ONE,
                BigInteger.ONE, BigInteger.ONE, BigInteger.ONE,
                BigInteger.ONE, BigInteger.ONE, BigInteger.valueOf(2)
        ), result);
    }

    @Test
    void testGetFractionWithZero() {

        assertTrue(AttackWiener.getFraction(BigInteger.TWO, BigInteger.ZERO).isEmpty());
    }

    @Test
    void testGetFractionLargeNumbers() {

        List<BigInteger> result = AttackWiener.getFraction(
                new BigInteger("123456789"),
                new BigInteger("987654321")
        );
        assertEquals(List.of(
                BigInteger.ZERO, BigInteger.valueOf(8),
                BigInteger.valueOf(13717421)
        ), result);
    }


    @Test
    void testBuildFractionSimple() {
        Pair<BigInteger, BigInteger> result = AttackWiener.buildFraction(
                List.of(BigInteger.ONE, BigInteger.ONE, BigInteger.valueOf(2))
        );
        assertEquals(BigInteger.valueOf(5), result.getKey());
        assertEquals(BigInteger.valueOf(3), result.getValue());
    }

    @Test
    void testBuildFractionSingleElement() {

        Pair<BigInteger, BigInteger> result = AttackWiener.buildFraction(
                List.of(BigInteger.valueOf(5))
        );
        assertEquals(BigInteger.valueOf(5), result.getKey());
        assertEquals(BigInteger.ONE, result.getValue());
    }

    @Test
    void testBuildFractionEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> {
            AttackWiener.buildFraction(List.of());
        });
    }

    @Test
    void testBuildFractionGoldenRatio() {

        Pair<BigInteger, BigInteger> result = AttackWiener.buildFraction(
                List.of(BigInteger.ONE, BigInteger.ONE, BigInteger.ONE,
                        BigInteger.ONE, BigInteger.ONE)
        );
        assertEquals(BigInteger.valueOf(8), result.getKey());
        assertEquals(BigInteger.valueOf(5), result.getValue());
    }

    @Test
    void testRoundTripConversion() {
        BigInteger a = new BigInteger("123456789");
        BigInteger b = new BigInteger("987654321");

        List<BigInteger> cf = AttackWiener.getFraction(a, b);
        Pair<BigInteger, BigInteger> fraction = AttackWiener.buildFraction(cf);

        assertEquals(BigInteger.valueOf(13717421), fraction.getKey());
        assertEquals(BigInteger.valueOf(109739369), fraction.getValue());
    }

    @Test
    void testRoundTripWithReduction() {
        BigInteger a = BigInteger.valueOf(15);
        BigInteger b = BigInteger.valueOf(25);

        List<BigInteger> cf = AttackWiener.getFraction(a, b);
        Pair<BigInteger, BigInteger> fraction = AttackWiener.buildFraction(cf);

        assertEquals(BigInteger.valueOf(3), fraction.getKey());
        assertEquals(BigInteger.valueOf(5), fraction.getValue());
    }



}
