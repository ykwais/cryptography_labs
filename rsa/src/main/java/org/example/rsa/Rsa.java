package org.example.rsa;

import javafx.util.Pair;
import lombok.Getter;
import org.example.buider_keys.KeysBuilder;
import org.example.models.CloseKey;
import org.example.models.OpenKey;

import java.math.BigInteger;

import static org.example.stateless.Math.powMod;

public class Rsa {

    public enum TestType {
        FERMAT,
        SOLOVAY_STRASSEN,
        MILLER_RABIN
    }

    private final KeysBuilder keysBuilder;
    @Getter
    private Pair<OpenKey, CloseKey> pairKeys;
    private int maxBitLength;


    public Rsa(TestType testType, int bitLength, double chance) {
        keysBuilder = new KeysBuilder(testType, bitLength, chance);
        pairKeys = keysBuilder.getKeys();
        maxBitLength = keysBuilder.getBitLengthN();
    }

    public void updateKeys() {
        pairKeys = keysBuilder.getKeys();
        maxBitLength = keysBuilder.getBitLengthN();
    }

    public BigInteger encrypt(BigInteger message) {
        if (message.bitLength() > maxBitLength) {
            throw new IllegalArgumentException("Message too long");
        }
        OpenKey open = pairKeys.getKey();
        return powMod(message, open.e() ,open.n());
    }

    public BigInteger decrypt(BigInteger cipher) {
        CloseKey close = pairKeys.getValue();
        return powMod(cipher, close.d(), close.n());
    }


}
