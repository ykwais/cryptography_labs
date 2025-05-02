package org.example.stateless;

import java.math.BigInteger;

public interface Math {

    static BigInteger gcd(BigInteger a, BigInteger b) {
        if (a == null || b == null) throw new IllegalArgumentException("null argument");
        if (BigInteger.ZERO.equals(a) && BigInteger.ZERO.equals(b)) throw new IllegalArgumentException("zero arguments");
        a = a.abs();
        b = b.abs();
        if(BigInteger.ZERO.equals(a)) return b;
        return gcd(b.mod(a), a);

    }

    static BigInteger[] gcdExpansion(BigInteger a, BigInteger b) {
        if (BigInteger.ZERO.equals(a)) {
            return new BigInteger[] {b, BigInteger.ZERO, BigInteger.ONE};
        }
        BigInteger[] result = gcdExpansion(b.mod(a), a);
        BigInteger x = result[2].subtract(result[1].multiply(b.divide(a)));
        BigInteger y = result[1];
        return new BigInteger[] {result[0], x, y};
    }


    static int legendre(BigInteger a, BigInteger p) {
        //p должен быть обязательно простым!!! //TODO добавить тест на простоту если получится
        if (p.compareTo(BigInteger.valueOf(3)) < 0 || p.mod(BigInteger.TWO).equals(BigInteger.ZERO)) throw new IllegalArgumentException("invalid n for Legendre symbol");

        BigInteger aByMod = a.mod(p);

        if (aByMod.compareTo(BigInteger.ZERO) == 0) return 0;

        BigInteger exponent = (p.subtract(BigInteger.ONE)).divide(BigInteger.TWO);
        BigInteger result = powMod(aByMod, exponent, p);

        return (result.compareTo(BigInteger.ONE) == 0) ? 1 : -1;
    }


    static int jacobi(BigInteger a, BigInteger n) {
        if (n.compareTo(BigInteger.valueOf(3)) < 0 || n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) throw new IllegalArgumentException("invalid n for Jacobi symbol");

        a = a.mod(n);
        if (a.equals(BigInteger.ZERO)) {
            return 0;
        }

        int result = 1;
        while (!a.equals(BigInteger.ZERO)) {

            while (a.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                a = a.divide(BigInteger.TWO);
                BigInteger mod8 = n.mod(BigInteger.valueOf(8));
                if (mod8.equals(BigInteger.valueOf(3)) || mod8.equals(BigInteger.valueOf(5))) {
                    result *= -1;
                }
            }

            BigInteger temp = a;
            a = n;
            n = temp;

            if (a.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)) &&
                    n.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
                result *= -1;
            }

            a = a.mod(n);
        }

        return n.equals(BigInteger.ONE) ? result : 0;
    }

    static BigInteger powMod(BigInteger a, BigInteger n, BigInteger m) {
        if (m.equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("Modulus cannot be zero");
        }

        BigInteger result = BigInteger.ONE;
        a = a.mod(m);

        while (n.compareTo(BigInteger.ZERO) > 0) {
            if (n.testBit(0)) {
                result = result.multiply(a).mod(m);
            }
            a = a.multiply(a).mod(m);
            n = n.shiftRight(1);
        }

        return result;
    }


    static int legendreQaudraticDependency(BigInteger a, BigInteger p) {
        if (p.compareTo(BigInteger.valueOf(3)) < 0 || p.mod(BigInteger.TWO).equals(BigInteger.ZERO)) throw new IllegalArgumentException("p is not even or < 3");


        a = a.mod(p);
        if (a.equals(BigInteger.ZERO)) {
            return 0;
        }

        int result = 1;
        while (!a.equals(BigInteger.ZERO)) {

            while (a.mod(BigInteger.TWO).equals(BigInteger.ZERO)) { // k раз делим на 2
                a = a.divide(BigInteger.TWO);
                BigInteger mod8 = p.mod(BigInteger.valueOf(8));
                if (mod8.equals(BigInteger.valueOf(3)) || mod8.equals(BigInteger.valueOf(5))) {
                    result *= -1;
                }
            }

            BigInteger temp = a;
            a = p;
            p = temp;

            if (a.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)) &&
                    p.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
                result *= -1;
            }

            a = a.mod(p);
        }

        return p.equals(BigInteger.ONE) ? result : 0;
    }



}
