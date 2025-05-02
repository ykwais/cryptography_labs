package org.example.simplyfility;

import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;


public abstract class BasePrimeAbstr implements SimplifilityInterface{

    SecureRandom rand = new SecureRandom();

    protected abstract boolean oneIteration(BigInteger checkingValue, BigInteger a);

    protected BigInteger denominatorParametr = BigInteger.TWO;

    @Override
    public boolean isSimple(BigInteger number, double chance) {
        if (Double.compare(chance, 0.5) < 0 || Double.compare(chance, 1.0) >= 0) throw new IllegalArgumentException("chance must be in [0.5, 1)");
        if (number.compareTo(BigInteger.ONE) <= 0) return false;
        if (number.compareTo(BigInteger.valueOf(3)) <= 0) return true;
        if (number.mod(BigInteger.TWO).equals(BigInteger.ZERO)) return false;

        BigInteger target = BigInteger.valueOf((long) Math.ceil(1 / (1 - chance)));
        BigInteger n = BigInteger.ONE;

        HashSet<BigInteger> checked = new HashSet<>();

        do {
            BigInteger a = getNextA(number, checked);
            if(!oneIteration(number, a)) {
                return false;
            }
            n = n.multiply(denominatorParametr);
        } while (n.compareTo(target) < 0);

        return true;
    }

    private BigInteger getNextA(BigInteger rightBound, HashSet<BigInteger> checked) {
        BigInteger candidate;
        BigInteger upperBound = rightBound.subtract(BigInteger.ONE);
        do {
            candidate = new BigInteger(upperBound.bitLength(), rand)
                    .mod(upperBound.subtract(BigInteger.ONE))
                    .add(BigInteger.valueOf(2));
        } while (checked.contains(candidate));

        checked.add(candidate);
        return candidate;
    }



}
