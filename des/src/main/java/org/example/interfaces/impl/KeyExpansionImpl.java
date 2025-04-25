package org.example.interfaces.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.utils.PermutationBits;
import org.example.constants.Tables;
import org.example.interfaces.KeyExpansion;

import static org.example.utils.ToView.bytesToHex;
import static org.example.utils.ToView.intToHex;

@Slf4j
public class KeyExpansionImpl implements KeyExpansion {
    @Override
    public byte[][] generateRoundKeys(byte[] key) {

        if (key.length != 8) {
            throw new IllegalArgumentException("Key must be 8 bytes (64 bit)");
        }

        byte[][] roundKeys = new byte[16][];

        byte[] c0d0 = PermutationBits.permute(key, Tables.PC1, true, true);

        log.info(bytesToHex(c0d0));

        int c0 = 0;
        for (int i = 0; i < 4; i++) {
            byte oneByte = c0d0[i];
            c0 |= oneByte & 0xFF;
            c0 <<= i == 3 ? 0 : 8;
        }

        c0 >>>= 4;

        log.info("c0 HEX: {}", intToHex(c0));

        int d0 = 0;
        for (int i = 3; i < 7; i++) {
            byte oneByte = c0d0[i];
            d0 |= oneByte & 0xFF;
            d0 <<= i == 6 ? 0 : 8;
        }

        d0 <<= 4;
        d0 >>>= 4;

        log.info("d0 HEX: {}", intToHex(d0));

        int cPrev = c0;
        int dPrev = d0;


        for (int i = 1; i <= 16; i++) {
            int amountBitTOShift = (i == 1 || i == 2 || i == 9 || i == 16) ? 1 : 2;
            int currentC = cycleLeftShiftFor28BitValue(cPrev, amountBitTOShift);
            int currentD = cycleLeftShiftFor28BitValue(dPrev, amountBitTOShift);

            int tmpC = currentC << 4;
            int tmpD = currentD;

            byte[] iRoundKey = new byte[7];

            for (int k = 3; k >= 0; k-- ) {
                iRoundKey[k] = (byte) (tmpC & 0xFF);
                tmpC = tmpC >> 8;
            }

            for (int k = 6; k>3; k--) {
                iRoundKey[k] = (byte) (tmpD & 0xFF);
                tmpD = tmpD >> 8;
            }

            iRoundKey[3] |= (byte) (tmpD & 0xFF);

            roundKeys[i-1] = PermutationBits.permute(iRoundKey, Tables.PC2, true, true);

            cPrev = currentC;
            dPrev = currentD;
        }

        return roundKeys;
    }


    public int cycleLeftShiftFor28BitValue(int value, int amountBitForShift) {
        return (( (value >> (28 - amountBitForShift) ) | (value << amountBitForShift))  & 0x0FFFFFFF);
    }

}
