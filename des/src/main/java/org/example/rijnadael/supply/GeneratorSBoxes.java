package org.example.rijnadael.supply;

import javafx.util.Pair;

import static org.example.rijnadael.stateless.GaloisOperations.getInversePolynom;

public class GeneratorSBoxes {

    private Pair<byte[], byte[]> sboxes = null;

    private byte leftCycleShift(byte a, int shift) {
        return (byte) ( (a << shift) | (a >>> (8 - shift)));
    }

    private byte multMatrixOnVector(byte invertedI) {
        return (byte)(invertedI ^ (leftCycleShift(invertedI, 4)) ^ (leftCycleShift(invertedI, 3)) ^ (leftCycleShift(invertedI, 2)) ^ (leftCycleShift(invertedI, 1)));
    }

    private Pair<byte[], byte[]> collectSBoxes(byte polynomeIrr) {
        byte[] sBox = new byte[256];
        byte[] invertedSBox = new byte[256];

        for (int i = 0; i < 256; i++) {
            byte invertedI = getInversePolynom((byte) i, polynomeIrr);
            byte valueInSbox = multMatrixOnVector(invertedI);
            valueInSbox ^= (byte) 0x63;

            sBox[i] = valueInSbox;
            invertedSBox[valueInSbox] = (byte) i;
        }
        return new Pair<>(sBox, invertedSBox);
    }

    public byte[] getSBox(byte polynomeIrr) {
        return getSBoxAndInvertedSBox(polynomeIrr).getKey().clone();
    }

    public byte[] getInvertedSBox(byte polynomeIrr) {
        return getSBoxAndInvertedSBox(polynomeIrr).getValue().clone();
    }

    private Pair<byte[], byte[]> getSBoxAndInvertedSBox(byte poly) {
        Pair<byte[], byte[]> result = sboxes;
        if (result == null) {
            synchronized (this) {
                result = sboxes;
                if (result == null) {
                    sboxes = result = collectSBoxes(poly);
                }
            }
        }
        return result;
    }
}
