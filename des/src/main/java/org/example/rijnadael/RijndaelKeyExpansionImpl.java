package org.example.rijnadael;

import org.example.interfaces.KeyExpansion;
import org.example.rijnadael.enums.RijndaelBlockLength;
import org.example.rijnadael.enums.RijndaelKeyLength;
import org.example.rijnadael.supply.GeneratorSBoxesAndRcon;

public class RijndaelKeyExpansionImpl implements KeyExpansion {

    private final int keyLength;
    private final int blockLength;
    private final int amountRounds;
    private final GeneratorSBoxesAndRcon generatorSBoxesAndRcon;

    public RijndaelKeyExpansionImpl(RijndaelKeyLength keyLength, RijndaelBlockLength blockLength, GeneratorSBoxesAndRcon generator) {
        this.keyLength = keyLength.getAmountOf4Bytes();
        this.blockLength = blockLength.getAmountOf4Bytes();
        this.amountRounds = Math.max(keyLength.getAmountOf4Bytes(), blockLength.getAmountOf4Bytes()) + 6;
        this.generatorSBoxesAndRcon = generator;
    }

    @Override
    public byte[][] generateRoundKeys(byte[] key) {
        int nb = blockLength;
        int nk = keyLength;
        int nr = amountRounds;

        int amountWords = (nr + 1) * nb;
        int sizeOfExpandedKey = amountWords * 4;
        byte[] expandedKey = new byte[sizeOfExpandedKey];
        System.arraycopy(key, 0, expandedKey, 0, nk * 4);

        byte[] tmp = new byte[4];
        byte[] w = new byte[4];

        for (int i = nk; i < amountWords; i++) {
            int prevWordIndex = (i - 1) * 4;
            System.arraycopy(expandedKey, prevWordIndex, tmp, 0, 4);

            if (i % nk == 0) {
                rotBytes(tmp);
                localSubBytes(tmp);
                xorByteArrays(tmp, generatorSBoxesAndRcon.getRcon()[i / nk]);
            } else if (nk > 6 && i % nk == 4) {
                localSubBytes(tmp);
            }

            System.arraycopy(expandedKey, (i - nk) * 4, w, 0, 4);
            xorByteArrays(tmp, w);
            System.arraycopy(tmp, 0, expandedKey, i * 4, 4);

        }

        byte[][] result = new byte[nr + 1][nb * 4];
        for (int i = 0; i <= nr; i++) {
            System.arraycopy(expandedKey, nb * 4 * i, result[i], 0, nb * 4);
        }

        return result;
    }


    private void xorByteArrays(byte[] a, byte[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays have different lengths");
        }
        for (int i = 0; i < a.length; ++i) {
            a[i] = (byte) (a[i] ^ b[i]);
        }
    }

    private void rotBytes(byte[] a) {
        if(a.length != 4) throw new IllegalArgumentException("Arrays length not 4!");
        byte tmp = a[0];
        a[0] = a[1];
        a[1] = a[2];
        a[2] = a[3];
        a[3] = tmp;
    }

    private void localSubBytes(byte[] a) {
        if (a.length != 4) throw new IllegalArgumentException("Arrays length not 4!");
        byte[] sBox = generatorSBoxesAndRcon.getSBox();
        for (int i = 0; i < a.length; ++i) {
            a[i] = sBox[a[i]];
        }
    }



}
