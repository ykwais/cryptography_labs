package org.example.rsa;

import javafx.util.Pair;
import org.example.buider_keys.KeysBuilder;
import org.example.models.CloseKey;
import org.example.models.OpenKey;
import org.example.simplyfility.SimplifilityInterface;

import java.math.BigInteger;

import static org.example.stateless.Math.powMod;

public class Rsa {

    public enum TestType {
        FERMAT,
        SOLOVAY_STRASSEN,
        MILLER_RABIN
    }

    private final TestType testType;
    private final int bitLength;
    private final double chance;
    private final KeysBuilder keysBuilder;
    private Pair<OpenKey, CloseKey> pairKeys;


    public Rsa(TestType testType, int bitLength, double chance) {
        this.testType = testType;
        this.bitLength = bitLength;
        this.chance = chance;
        keysBuilder = new KeysBuilder(testType, bitLength, chance);
        pairKeys = keysBuilder.getKeys();
    }



    public BigInteger encrypt(BigInteger message) {
        OpenKey open = pairKeys.getKey();
        return powMod(message, open.e() ,open.n());
    }

    public BigInteger decrypt(BigInteger cipher) {
        CloseKey close = pairKeys.getValue();
        return powMod(cipher, close.d(), close.n());
    }


}
