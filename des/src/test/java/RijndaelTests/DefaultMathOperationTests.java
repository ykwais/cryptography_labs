package RijndaelTests;

import org.example.rijnadael.stateless.GaloisOperations;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.example.rijnadael.stateless.GaloisBig.factorize;
import static org.example.rijnadael.stateless.GaloisOperations.*;
import static org.example.rijnadael.supply.GeneratorSBoxesAndRcon.multMatrixOnVector;
import static org.example.utils.ToView.formatShortToBinary;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultMathOperationTests {

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

        System.out.println("!!!!!!!!!!!!!!!!");

        List<Short> res2 = GaloisOperations.calculateAllIrrediciblePolynoms(5);
        for (int i = 0; i < res2.size(); i++) {
            System.out.println(i + 1 + ") " + formatShortToBinary(res2.get(i)));
        }
    }

    @Test
    void checkOpposite() {
        byte start = (byte) 0x02;
        byte oppo = getInversePolynom(start, (byte) 0x1B);
        System.out.println(oppo);
        assertEquals((byte) 0x01, multiplyPolymomsByMod(start, oppo, (byte) 0x1B));

    }

    @Test
    void checkAllInverses() {
        byte poly = (byte) 0x1B;

        for (int i = 1; i < 256; i++) {
            byte a = (byte) i;
            byte inv = getInversePolynom(a, poly);

            byte product = multiplyPolymomsByMod(a, inv, poly);

            assertEquals((byte) 0x01, product,
                    String.format("Inverse error: %02X * %02X mod %02X = %02X", a & 0xFF, inv & 0xFF, poly & 0xFF, product & 0xFF));
        }
    }


    @Test
    void testAffineTransformationExample() {
        byte input = 0x00;
        byte expected = 0x63; // по стандарту AES

        byte actual = (byte) (multMatrixOnVector(input) ^ (byte) 0x63);

        assertEquals(expected, actual, String.format("Affine error: expected 0x%02X, got 0x%02X", expected, actual));
    }





    @Test
    void testFactorizationShort() {
        short poly = (short) 0b100101; // x^5 + x^2 + 1 - уже неприводимый
        List<Short> factors = factorizePolynomial(poly);

        System.out.println("Разложение полинома:");
        for (Short f : factors) {
            System.out.println(Integer.toBinaryString(f & 0xFFFF));
        }

        short poly2 = (short) 0b1100101; // x^6 + x^5 + x^2 + 1 = (x + 1)(x^2 + x + 1)(x^3 + x^2 + 1)
        List<Short> factors2 = factorizePolynomial(poly2);

        System.out.println("Разложение полинома:");
        for (Short f : factors2) {
            System.out.println(Integer.toBinaryString(f & 0xFFFF));
        }

        short poly3 = (short) 0b1; // 1
        List<Short> factors3 = factorizePolynomial(poly3);

        System.out.println("Разложение полинома:");
        for (Short f : factors3) {
            System.out.println(Integer.toBinaryString(f & 0xFFFF));
        }

        short poly4 = (short) 0b0; // 0
        List<Short> factors4 = factorizePolynomial(poly4);

        System.out.println("Разложение полинома:");
        for (Short f : factors4) {
            System.out.println(Integer.toBinaryString(f & 0xFFFF));
        }
    }

    @Test
    void testBigFactorization() {
        BigInteger poly = new BigInteger("100101", 2);

        List<BigInteger> factors = factorize(poly);

        System.out.println("Факторизация " + poly.toString(2) + ":");
        for (BigInteger f : factors) {
            System.out.println(f.toString(2));
        }



        BigInteger poly2 = new BigInteger("1100101", 2);

        List<BigInteger> factors2 = factorize(poly2);

        System.out.println("Факторизация " + poly2.toString(2) + ":");
        for (BigInteger f : factors2) {
            System.out.println(f.toString(2));
        }



        BigInteger poly3 = new BigInteger("1", 2);

        List<BigInteger> factors3 = factorize(poly3);

        System.out.println("Факторизация " + poly3.toString(2) + ":");
        for (BigInteger f : factors3) {
            System.out.println(f.toString(2));
        }


        BigInteger poly4 = new BigInteger("0", 2);

        List<BigInteger> factors4 = factorize(poly4);

        System.out.println("Факторизация " + poly4.toString(2) + ":");
        for (BigInteger f : factors4) {
            System.out.println(f.toString(2));
        }


        BigInteger poly5 = new BigInteger("010000001000000001010000", 2);

        List<BigInteger> factors5 = factorize(poly5);

        System.out.println("Факторизация " + poly5.toString(2) + ":");
        for (BigInteger f : factors5) {
            System.out.println(f.toString(2));
        }
    }

}
