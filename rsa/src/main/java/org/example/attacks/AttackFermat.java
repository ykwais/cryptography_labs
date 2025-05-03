package org.example.attacks;

import javafx.util.Pair;

import java.math.BigInteger;

import static org.example.stateless.Math.gcdExpansion;

public class AttackFermat {

    public Pair<BigInteger, BigInteger> calculate(BigInteger e, BigInteger n) {
        BigInteger phi = getPhi(n);

        BigInteger[] resultExpandedEuclid = gcdExpansion(e, phi);

        if (!resultExpandedEuclid[0].equals(BigInteger.ONE)) {
            throw new IllegalStateException("gcdExpansion(e, phi) != 1");
        }

        BigInteger d = resultExpandedEuclid[1];
        if (d.compareTo(BigInteger.ZERO) < 0) {
            d = d.add(phi);
        }

        return new Pair<>(d, phi);

    }

    private static BigInteger getPhi(BigInteger n) {
        BigInteger tmp = n.sqrt();
        BigInteger a = tmp.multiply(tmp).equals(n) ? tmp : tmp.add(BigInteger.ONE);

        BigInteger bSqr = a.multiply(a).subtract(n);
        BigInteger b = bSqr.sqrt();

        while (!b.multiply(b).equals(bSqr)) {
            a = a.add(BigInteger.ONE);
            bSqr = a.multiply(a).subtract(n);
            b = bSqr.sqrt();
        }

        BigInteger p = a.add(b);
        BigInteger q = a.subtract(b);

        return p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
    }
}
