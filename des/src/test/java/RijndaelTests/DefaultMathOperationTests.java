package RijndaelTests;

import org.example.rijnadael.GaloisOperations;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.example.utils.ToView.formatShortToBinary;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultMathOperationTests {

    @Test
    void testAddingPolynomous() {
        assertEquals((byte)0x81, GaloisOperations.addPolys((byte) 0x50, (byte) 0xD1));
    }

    @Test
    void testAddingPolynomousSelf() {
        assertEquals((byte) 0x00, GaloisOperations.addPolys((byte) 0x50, (byte) 0x50));
    }

    @Test
    void testMultiOnX() {
        assertEquals((byte) 0x9B, GaloisOperations.multOnX((byte) 0xC0, (byte) 0x1B));
    }

    @Test
    void testMultiOnXZero() {
        assertEquals((byte) 0x00, GaloisOperations.multOnX((byte) 0x00, (byte) 0x1B));
    }

    @Test
    void testMultiplyPolynoms1() {
        assertEquals((byte) 0xC1, GaloisOperations.multiplyPolymomsByMod((byte) 0x57, (byte) 0x83, (byte) 0x1B));
    }

    @Test
    void testMultiplyPolynoms2() {
        assertEquals((byte) 0x76, GaloisOperations.multiplyPolymomsByMod((byte) 0xC0 , (byte) 0x07, (byte) 0x1B));
    }

    @Test
    void testDegree() {
        assertEquals(7, GaloisOperations.degree((short) 0x80));
    }

    @Test
    void testDivideAndModSmaller() {
        short[] res = GaloisOperations.divideAndMod((short) 0x10, (short) 0x11B);
        assertEquals((short) 0x00, res[0]);
        assertEquals((short) 0x10, res[1]);
    }

    @Test
    void testDevide() {
        assertEquals((short)0x01, GaloisOperations.divide((short) 0x180, (short) 0x11B));
    }

    @Test
    void testMod() {
        assertEquals((short)0x9B, GaloisOperations.mod((short) 0x180, (short) 0x11B));
    }

    @Test
    void testPowMod() {
        assertEquals((byte) 0x01, GaloisOperations.powMod((byte) 0x03, 0, (byte) 0x1B));
        assertEquals((byte) 0x03, GaloisOperations.powMod((byte) 0x03, 1, (byte) 0x1B));
        assertEquals((byte) 0x33, GaloisOperations.powMod((byte) 0x03, 5, (byte) 0x1B));
    }

    @Test
    void testAllIrredicible() {
        List<Short> res = GaloisOperations.calculateAllIrrediciblePolynoms(8);
        assertEquals(30, res.size());
        for (int i = 1; i <= res.size(); i++) {
            System.out.println(i + ") " + formatShortToBinary(res.get(i-1)));
        }
    }

}
