package org.example.simplyfility.tests;

import org.example.simplyfility.BasePrimeAbstr;

import java.math.BigInteger;

import static org.example.stateless.Math.powBIGs;
import static org.example.stateless.Math.powMod;

public class MillerRabinTest extends BasePrimeAbstr {
    public MillerRabinTest() {
        this.denominatorParametr = BigInteger.valueOf(4);
    }

    @Override
    protected boolean oneIteration(BigInteger checkingValue, BigInteger a) {
        BigInteger s = BigInteger.ZERO;
        BigInteger d = checkingValue.subtract(BigInteger.ONE);
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            s = s.add(BigInteger.ONE);
            d = d.divide(BigInteger.TWO);
        }

        if (powMod(a,d,checkingValue).equals(BigInteger.ONE)) {
            return true;
        }

        for (BigInteger r = BigInteger.ZERO; r.compareTo(s) < 0; r = r.add(BigInteger.ONE)) {
            BigInteger tmp = powMod(a, powBIGs(BigInteger.TWO, r).multiply(d), checkingValue);
            if (tmp.equals(checkingValue.subtract(BigInteger.ONE))) {
                return true;
            }
        }
        return false;
    }
}
