package org.example.rijnadael;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public interface GaloisBig {
    static int degree(BigInteger poly) {
        return poly.bitLength() - 1;
    }

    static BigInteger[] divideAndMod(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO))
            throw new ArithmeticException("Division by zero");

        int degreeSecond = degree(b);
        BigInteger full = BigInteger.ZERO;
        BigInteger mod = a;
        while (degree(mod) >= degreeSecond) {
            int shift = degree(mod) - degreeSecond;
            full = full.setBit(shift);
            BigInteger term = b.shiftLeft(shift);
            mod = mod.xor(term);
        }

        return new BigInteger[]{full, mod};
    }

    static boolean isIrreducible(BigInteger poly, int degree) {
        if (poly.equals(BigInteger.ZERO)) return false;
        if (degree == 1) return true;

        int maxDivisorDegree = degree / 2;
        BigInteger upperBound = BigInteger.ONE.shiftLeft(maxDivisorDegree + 1);

        for (BigInteger i = BigInteger.TWO; i.compareTo(upperBound) < 0; i = i.add(BigInteger.ONE)) {
            if (divideAndMod(poly, i)[1].equals(BigInteger.ZERO)) {
                return false;
            }
        }
        return true;
    }


    static List<BigInteger> getAllIrreduciblePolynomials(int targetDegree) {
        List<BigInteger> result = new ArrayList<>();
        BigInteger start = BigInteger.ONE.shiftLeft(targetDegree);
        BigInteger end = start.shiftLeft(1);

        for (BigInteger i = start; i.compareTo(end) < 0; i = i.add(BigInteger.ONE)) {
            if (isIrreducible(i, targetDegree)) {
                result.add(i);
            }
        }
        return result;
    }

    static List<BigInteger> factorize(BigInteger poly) {
        List<BigInteger> factors = new ArrayList<>();

        if (poly.equals(BigInteger.ZERO)) return factors;
        if (poly.equals(BigInteger.ONE)) {
            factors.add(BigInteger.ONE);
            return factors;
        }

        int deg = degree(poly);
        if (deg == 0) {
            factors.add(poly);
            return factors;
        }

        if (isIrreducible(poly, deg)) {
            factors.add(poly);
            return factors;
        }

        for (int d = 1; d <= deg/2; d++) {
            for (BigInteger candidate : getAllIrreduciblePolynomials(d)) {
                while (true) {
                    BigInteger[] divResult = divideAndMod(poly, candidate);
                    if (!divResult[1].equals(BigInteger.ZERO)) break;

                    factors.add(candidate);
                    poly = divResult[0];

                    if (poly.equals(BigInteger.ONE)) return factors;

                    int newDeg = degree(poly);
                    if (newDeg > 0 && isIrreducible(poly, newDeg)) {
                        factors.add(poly);
                        return factors;
                    }
                }
            }
        }

        return factors;
    }
}
