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

@Setter
@Getter
@Slf4j
public class KeysBuilder {
    private final SimplifilityInterface test;
    private final double chance;
    private final int bitLength;

    private final SecureRandom random = new SecureRandom();

    public BigInteger publicExp =  BigInteger.valueOf(65537);

    public KeysBuilder(Rsa.TestType type, int bitLength, double chance) {
        this.bitLength = bitLength;
        this.chance = chance;
        test =
                switch (type) {
                    case FERMAT -> new TestFerma();
                    case SOLOVAY_STRASSEN -> new SolovayStrassenTest();
                    case MILLER_RABIN -> new MillerRabinTest();
                    default -> throw new IllegalStateException("Unexpected value: " + type);
                };
    }

    public Pair<OpenKey, CloseKey> getKeys(){

        Pair<BigInteger, BigInteger> pAndQ = generatePQ();

        BigInteger p = pAndQ.getKey();
        BigInteger q = pAndQ.getValue();

        BigInteger n = p.multiply(q);

        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        if (!gcd(publicExp, phi).equals(BigInteger.ONE)) {
            throw new IllegalStateException("gcd(phi, e) != 1");
        }

        BigInteger[] resultExpandedEuclid = gcdExpansion(publicExp, phi);

        if (!resultExpandedEuclid[0].equals(BigInteger.ONE)) {
            throw new IllegalStateException("gcdExpansion(publicExp, phi) != 1");
        }

//        BigInteger tmp1 = resultExpandedEuclid[1].mod(phi).add(phi).mod(phi);
//        BigInteger tmp2 = resultExpandedEuclid[2].mod(phi).add(phi).mod(phi);

        BigInteger d = resultExpandedEuclid[1];
        log.info("d: {}", d);
        log.info("phi : {}", phi);
        if (d.compareTo(BigInteger.ZERO) < 0) {
            d = d.add(phi);
        }
        log.info("d: {}", d);

        OpenKey open = new OpenKey(publicExp, n);
        CloseKey close = new CloseKey(d, n);

        return new Pair<>(open, close);
    }

    private BigInteger getPorQ(boolean isP) {
        BigInteger res;
        do {
            res = isP ? new BigInteger(bitLength, random).setBit(0).setBit(bitLength-1).clearBit(bitLength-2) : new BigInteger(bitLength, random).setBit(0).setBit(bitLength-1).setBit(bitLength-2);
        } while (!test.isSimple(res, chance));
        return res;
    }

    private Pair<BigInteger, BigInteger> generatePQ() {
        BigInteger p = getPorQ(true);
        BigInteger q,n;
        do {
            q = getPorQ(false);
            n = p.multiply(q);
        } while (p.subtract(q).abs().compareTo(powBIGs(BigInteger.TWO, BigInteger.valueOf( (n.bitLength()/2 - 100) ))) < 0);
        return new Pair<>(p, q);
    }





}
