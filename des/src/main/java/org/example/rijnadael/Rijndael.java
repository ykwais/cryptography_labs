package org.example.rijnadael;

import lombok.Getter;
import org.example.interfaces.EncryptorDecryptorSymmetric;
import org.example.interfaces.KeyExpansion;
import org.example.rijnadael.enums.RijndaelBlockLength;
import org.example.rijnadael.enums.RijndaelKeyLength;
import org.example.rijnadael.supply.GeneratorSBoxesAndRcon;
import org.example.rijnadael.supply.PolinomWithGf;

import java.util.Arrays;

public class Rijndael implements EncryptorDecryptorSymmetric {
    private byte[] key = null;
    private final KeyExpansion keyExpansion;
    private final int blockSize;
    private byte[] sBox = null;
    private byte[] invertedSBox = null;
    private int nb = 0;

    PolinomWithGf cX = new PolinomWithGf((byte) 0x03, (byte) 0x01, (byte) 0x01, (byte) 0x02);
    PolinomWithGf dX = new PolinomWithGf((byte) 0x0B, (byte) 0x0D, (byte) 0x09, (byte) 0x0E);

    @Getter
    private final byte polynomeIrr;

    private final GeneratorSBoxesAndRcon generatorSBoxesAndRcon;


    public Rijndael(RijndaelKeyLength keyLength, RijndaelBlockLength blockLength, byte[] key, byte poly) {
        polynomeIrr = poly;
        generatorSBoxesAndRcon = new GeneratorSBoxesAndRcon(polynomeIrr);
        keyExpansion = new RijndaelKeyExpansionImpl(keyLength, blockLength, generatorSBoxesAndRcon);
        blockSize = blockLength.getAmountOf4Bytes() * 4;
        if(!checkAmountBytesInKey(key, keyLength)) {
            throw new IllegalArgumentException("Amount of bytes in key does not match with your parameters");
        }
        setKey(key);
        generatorSBoxesAndRcon.setParams(blockLength.getAmountOf4Bytes(), keyLength.getAmountOf4Bytes());
        nb = blockLength.getAmountOf4Bytes();
    }

//    public void setPolynomeIrr(byte polynomeIrr) {
//        this.polynomeIrr = polynomeIrr;
//        sBox = null;
//        invertedSBox = null;
//        generatorSBoxesAndRcon.setPoly(polynomeIrr);
//    }

    @Override
    public void setKey(byte[] symmetricKey) {
        if (this.key != null && key.length != symmetricKey.length) {
            throw new IllegalArgumentException("The symmetric key length does not match with the key length");
        }
        this.key = symmetricKey;
    }

    @Override
    public byte[] encrypt(byte[] message) {
        return encryptDecryptInner(message, key, true);
    }

    @Override
    public byte[] decrypt(byte[] cipherText) {
        return encryptDecryptInner(cipherText, key, false);
    }

    @Override
    public int getBlockSize() {
        return blockSize;
    }

    private boolean checkAmountBytesInKey(byte[] key, RijndaelKeyLength keyLength) {
        return key.length == keyLength.getAmountOf4Bytes() * 4;
    }

    public byte[] encryptDecryptInner(byte[] oneBlock, byte[] key, boolean isEncrypt) {

        byte[][] roundKeys = this.keyExpansion.generateRoundKeys(key);
        byte[] acc = oneBlock.clone();

        if(isEncrypt) {
            addRoundKey(acc, roundKeys[0]);
            // цикл
            for (int i = 1; i < roundKeys.length-1; i++ ) {
                subBytes(acc);
                shiftRows(acc);
                mixColumns(acc, true);
                addRoundKey(acc, roundKeys[i]);
            }
            // final round
            subBytes(acc);
            shiftRows(acc);
            addRoundKey(acc, roundKeys[roundKeys.length - 1]);
        } else {
            // inverted final round
            addRoundKey(acc, roundKeys[roundKeys.length - 1]);
            reversedShiftRows(acc);
            reverseSubBytes(acc);
            // reverse cycle
            for (int i = roundKeys.length - 2; i > 0; i--) {
                addRoundKey(acc, roundKeys[i]);
                mixColumns(acc, false);
                reversedShiftRows(acc);
                reverseSubBytes(acc);
            }
            addRoundKey(acc, roundKeys[0]);
        }


        return acc;
    }



    public void subBytes(byte[] oneBlock) {

        if (sBox == null) {
            sBox = generatorSBoxesAndRcon.getSBox();
        }

        for (int i = 0; i < oneBlock.length; i++) {
            oneBlock[i] =  (sBox[oneBlock[i] & 0xFF]);
        }
    }

    public void reverseSubBytes(byte[] oneBlock) {
        if (invertedSBox == null) {
            invertedSBox = generatorSBoxesAndRcon.getInvertedSBox();
        }

        for (int i = 0; i < oneBlock.length; i++) {
            oneBlock[i] = (invertedSBox[oneBlock[i] & 0xFF]);
        }
    }



    public void addRoundKey(byte[] a, byte[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays have different lengths");
        }
        for (int i = 0; i < a.length; ++i) {
            a[i] = (byte) (a[i] ^ b[i]);
        }
    }

    public void shiftRows(byte[] oneBlock) {
        byte[] tmp = Arrays.copyOf(oneBlock, oneBlock.length);
        for (int row = 1; row < 4; row++) {
            for (int col = 0; col < nb; col++) {
                int newPos = (col - row + nb) % nb;
                oneBlock[row + 4 * newPos] = (byte) (tmp[row + 4 * col] & 0xFF);
            }
        }
    }

    public void reversedShiftRows(byte[] oneBlock) {
        byte[] tmp = Arrays.copyOf(oneBlock, oneBlock.length);
        for (int row = 1; row < 4; row++) {
            for (int col = 0; col < nb; col++) {
                int newPos = (col + row) % nb;
                oneBlock[row + 4 * newPos] = (byte) (tmp[row + 4 * col] & 0xFF);
            }
        }
    }

    public void mixColumns(byte[] oneBlock, boolean isEncrypt) {
        for(int i = 0; i < nb; i++) {
            PolinomWithGf currentColumnPoly = new PolinomWithGf(oneBlock[4*i + 3], oneBlock[4*i + 2],oneBlock[4*i + 1],oneBlock[4*i]);
            currentColumnPoly = PolinomWithGf.mult(currentColumnPoly, (isEncrypt) ? cX : dX, polynomeIrr);
            oneBlock[4*i + 3] = (byte) (currentColumnPoly.d3() & 0xFF);
            oneBlock[4*i + 2] = (byte) (currentColumnPoly.d2() & 0xFF);
            oneBlock[4*i + 1] = (byte) (currentColumnPoly.d1() & 0xFF);
            oneBlock[4*i] = (byte) (currentColumnPoly.d0() & 0xFF);
        }
    }







}
