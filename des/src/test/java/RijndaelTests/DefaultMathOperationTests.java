package RijndaelTests;

import org.example.rijnadael.GaloisOperations;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultMathOperationTests {

    @Test
    void testAddingPolynomous() {
        assertEquals((byte)0x81, GaloisOperations.addPolys((byte) 0x50, (byte) 0xD1));
    }

    @Test
    void testMultiOnX() {
        assertEquals((byte) 0x9B, GaloisOperations.multOnX((byte) 0xC0, (byte) 0x1B));
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
        assertEquals(4, GaloisOperations.degree((short)0x1B));
    }


}
