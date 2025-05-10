package org.example.rijnadael.supply;

import javafx.util.Pair;

import static org.example.rijnadael.stateless.GaloisOperations.*;

public class GeneratorSBoxesAndRcon {

    private byte poly;

    private Pair<byte[], byte[]> sboxes = null;

    private byte[][] rcon = null;

    private int nb = 0;
    private int nk = 0;
    private int nr = 0;


    public GeneratorSBoxesAndRcon(byte poly) {
        this.poly = poly;
    }


    public void setPoly(byte poly) {
        this.poly = poly;
        sboxes = null;
    }

    public void setParams(int nb, int nk) {
        this.nb = nb;
        this.nk = nk;
        this.nr = Math.max(nb, nk) + 6;
    }



    public static byte leftCycleShift(byte a, int shift) {
        return (byte) ( (a << shift) | (a >>> (8 - shift)));
    }

    public static byte multMatrixOnVector(byte invertedI) {
        return (byte)(invertedI ^ (leftCycleShift(invertedI, 4)) ^ (leftCycleShift(invertedI, 3)) ^ (leftCycleShift(invertedI, 2)) ^ (leftCycleShift(invertedI, 1)));
    }

    private Pair<byte[], byte[]> collectSBoxes() {
        byte[] sBox = new byte[256];
        byte[] invertedSBox = new byte[256];

        for (int i = 0; i < 256; i++) {
//            byte invertedI = getInversePolynom((byte) (i & 0xFF), poly);
//            byte valueInSbox = multMatrixOnVector(invertedI);
//            valueInSbox ^= (byte) 0x63;
//
//            sBox[i] = (byte) (valueInSbox & 0xFF);
//            invertedSBox[valueInSbox & 0xFF] = (byte) i;
            byte inv;
            if (i == 0) {
                inv = 0;
            } else {
                inv = getInversePolynom((byte) i, poly);
            }

            byte valueInSbox = (byte) (multMatrixOnVector(inv) ^ (byte)0x63);
            int sValue = valueInSbox & 0xFF;

            sBox[i] = (byte) sValue;
            invertedSBox[sValue] = (byte) i;
        }
        return new Pair<>(sBox, invertedSBox);
    }

    public byte[] getSBox() {
        return getSBoxAndInvertedSBox().getKey().clone();
    }

    public byte[] getInvertedSBox() {
        return getSBoxAndInvertedSBox().getValue().clone();
    }

    private Pair<byte[], byte[]> getSBoxAndInvertedSBox() {
        Pair<byte[], byte[]> result = sboxes;
        if (result == null) {
            synchronized (this) {
                result = sboxes;
                if (result == null) {
                    sboxes = result = collectSBoxes();
                }
            }
        }
        return result;
    }

    public byte[][] getRcon() {
        if (nb == 0 || nk == 0 || nr == 0) {
            throw new IllegalArgumentException("invalid value of parameters");
        }
        return getRconInner().clone();
    }

    private byte[][] getRconInner() {
        byte[][] result = rcon;
        if (result == null) {
            synchronized (this) {
                result = rcon;
                if (result == null) {
                    rcon = result = generateRcon();
                }
            }
        }
        return result;
    }

    private byte[][] generateRcon() {
        int size = (nr + 1) * 4 * nb;
        byte[][] result = new byte[size][];
        result[0] = new byte[4];
        result[1] = new byte[4];
        result[1][0] = 1;
        for(int i = 2; i < size; i++) {
            byte prev = result[i-1][0];
            byte next = multOnX(prev, poly);
            result[i] = new byte[4];
            result[i][0] = next;
        }
        return result;
    }



}
