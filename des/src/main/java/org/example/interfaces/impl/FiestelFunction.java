package org.example.interfaces.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.constants.Tables;
import org.example.interfaces.EncryptionTransformation;
import org.example.utils.PermutationBits;


@Slf4j
public class FiestelFunction implements EncryptionTransformation {

    @Override
    public byte[] doFunction(byte[] right, byte[] roundKey) {

        byte[] rightAfterExpansion = PermutationBits.permute(right, Tables.E, true, true);

        byte[] afterXor = xorByteArrays(rightAfterExpansion, roundKey);


        long forDivisionOn6Bit = 0;

        for (int i = 0; i < 6; i++) {
            byte oneByte = afterXor[i];
            forDivisionOn6Bit |= oneByte & 0xFF;
            forDivisionOn6Bit <<= i == 5 ? 0 : 8;
        }

        byte[] bytesBy6Bit = new byte[8];

        for (int i = 7; i >= 0; --i) {
            bytesBy6Bit[i] = (byte) (forDivisionOn6Bit & 0x3F);
            forDivisionOn6Bit >>>= 6;
        }



        // на данном этапе имеется массив размера 8 на каждые 6 бит

        int preResult = 0;

        for (int i = 0; i < 8; ++i) {
            byte oneByte = bytesBy6Bit[i];
            int row = (((oneByte & 0x20) >>> 4) | (oneByte & 0x01) ) & 0xFF;
            int col = ((oneByte & 0x1E) >>> 1) & 0xFF;

            preResult |= (Tables.S[i][row][col]) & 0x0F;
            preResult <<= i == 7 ? 0 : 4;
        }

        byte[] intToByteArrayForPPermutation = new byte[4];


        for (int i = 3; i >= 0; i--) {
            intToByteArrayForPPermutation[i] = (byte) (preResult & 0xFF);
            preResult >>>= 8;
        }

        return PermutationBits.permute(intToByteArrayForPPermutation, Tables.P, true, true);
    }

    private byte[] xorByteArrays(byte[] a, byte[] b) {

        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays have different lengths");
        }

        byte[] c = new byte[a.length];
        for (int i = 0; i < c.length; i++) {
            c[i] = (byte) (a[i] ^ b[i]);
        }
        return c;
    }
}
