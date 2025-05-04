package org.example.buider_keys;

import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.models.CloseKey;
import org.example.models.OpenKey;
import org.example.rsa.Rsa;
import org.example.simplyfility.SimplifilityInterface;
import org.example.simplyfility.tests.MillerRabinTest;
import org.example.simplyfility.tests.SolovayStrassenTest;
import org.example.simplyfility.tests.TestFerma;

import java.math.BigInteger;
import java.security.SecureRandom;

import static org.example.stateless.Math.*;

@Slf4j
public class KeysBuilder {
    private final SimplifilityInterface test;
    private final double chance;
    private final int bitLength;
    private int maxBitLength;

    private final SecureRandom random = new SecureRandom();

    @Getter
    @Setter
    BigInteger publicExp =  BigInteger.valueOf(65537);

    public KeysBuilder(Rsa.TestType type, int bitLength, double chance) {
        this.bitLength = bitLength;
        this.chance = chance;
        test =
                switch (type) {
                    case FERMAT -> new TestFerma();
                    case SOLOVAY_STRASSEN -> new SolovayStrassenTest();
                    case MILLER_RABIN -> new MillerRabinTest();
                };
    }

    public Pair<OpenKey, CloseKey> getKeys(){

        BigInteger p;
        BigInteger q;
        BigInteger d;
        BigInteger n;
        BigInteger phi;
        CloseKey close;
        OpenKey open;

        do {
            Pair<BigInteger, BigInteger> pAndQ = generatePQ();

            p = pAndQ.getKey();
            q = pAndQ.getValue();

            n = p.multiply(q);

            log.info("bit length of N: {}", n.bitLength());

            phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

            BigInteger[] resultExpandedEuclid = gcdExpansion(publicExp, phi);

            d = resultExpandedEuclid[1];
            log.info("d: {}", d);
            log.info("phi : {}", phi);
            if (d.compareTo(BigInteger.ZERO) < 0) {
                d = d.add(phi);
            }
            log.info("d: {}", d);

            open = new OpenKey(publicExp, n);
            close = new CloseKey(d, n);

        } while (powBIGs(d, BigInteger.valueOf(4)).multiply(BigInteger.valueOf(81)).compareTo(n) < 0 || !gcd(publicExp, phi).equals(BigInteger.ONE)); // против Винера

        maxBitLength = n.bitLength();

        return new Pair<>(open, close);
    }

    private BigInteger getPorQ(boolean isP) {
        BigInteger res;
        do {
            BigInteger bigInteger = new BigInteger(bitLength, random).setBit(0).setBit(bitLength - 1);
            res = isP ? bigInteger.clearBit(bitLength-2) : bigInteger.setBit(bitLength-2);
        } while (!test.isSimple(res, chance));
        return res;
    }

    private Pair<BigInteger, BigInteger> generatePQ() {
        BigInteger p = getPorQ(true);
        BigInteger q;
        BigInteger n;
        do {
            q = getPorQ(false);
            n = p.multiply(q);
        } while (p.subtract(q).abs().compareTo(powBIGs(BigInteger.TWO, BigInteger.valueOf( (n.bitLength()/2 - 100) ))) < 0);//против Ферма
        return new Pair<>(p, q);
    }

    public int getBitLengthN() {
        return maxBitLength;
    }

}
