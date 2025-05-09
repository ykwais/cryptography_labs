package org.example.rijnadael;

import org.example.interfaces.KeyExpansion;
import org.example.rijnadael.enums.RijndaelBlockLength;
import org.example.rijnadael.enums.RijndaelKeyLength;

public class RijndaelKeyExpansionImpl implements KeyExpansion {

    private final int keyLength;
    private final int blockLength;
    private final int amountRounds;

    public RijndaelKeyExpansionImpl(RijndaelKeyLength keyLength, RijndaelBlockLength blockLength) {
        this.keyLength = keyLength.getAmountOf4Bytes();
        this.blockLength = blockLength.getAmountOf4Bytes();
        this.amountRounds = Math.max(keyLength.getAmountOf4Bytes(), blockLength.getAmountOf4Bytes()) + 6;
    }

    @Override
    public byte[][] generateRoundKeys(byte[] key) {
        int Nb = blockLength;
        int Nk = keyLength;
        int Nr = amountRounds;

        return new byte[0][0];
    }
}
