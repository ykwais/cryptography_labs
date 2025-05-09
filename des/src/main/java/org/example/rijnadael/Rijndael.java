package org.example.rijnadael;

import lombok.Getter;
import org.example.interfaces.EncryptorDecryptorSymmetric;
import org.example.interfaces.KeyExpansion;
import org.example.rijnadael.enums.RijndaelBlockLength;
import org.example.rijnadael.enums.RijndaelKeyLength;
import org.example.rijnadael.supply.GeneratorSBoxesAndRcon;

import java.util.Arrays;

public class Rijndael implements EncryptorDecryptorSymmetric {
    private byte[] key;
    private final KeyExpansion keyExpansion;
    private final int blockSize;
    private byte[] sBox = null;
    private byte[] invertedSBox = null;
    private int nb = 0;

    @Getter
    private byte polynomeIrr = 0x1B;

    private final GeneratorSBoxesAndRcon generatorSBoxesAndRcon = new GeneratorSBoxesAndRcon(polynomeIrr);


    public Rijndael(RijndaelKeyLength keyLength, RijndaelBlockLength blockLength, byte[] key) {
        keyExpansion = new RijndaelKeyExpansionImpl(keyLength, blockLength, generatorSBoxesAndRcon);
        blockSize = blockLength.getAmountOf4Bytes() * 4;
        if(!checkAmountBytesInKey(key, keyLength)) {
            throw new IllegalArgumentException("Amount of bytes in key does not match with your parameters");
        }
        setKey(key);
        generatorSBoxesAndRcon.setParams(blockLength.getAmountOf4Bytes(), keyLength.getAmountOf4Bytes());
        nb = blockLength.getAmountOf4Bytes();
    }

    public void setPolynomeIrr(byte polynomeIrr) {
        this.polynomeIrr = polynomeIrr;
        sBox = null;
        invertedSBox = null;
        generatorSBoxesAndRcon.setPoly(polynomeIrr);
    }

    @Override
    public void setKey(byte[] symmetricKey) {
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

        if(isEncrypt) {
            addRoundKey(oneBlock, roundKeys[0]);
            // цикл
            // final round
        } else {
            // inverted final round
            // reverse cycle
            addRoundKey(oneBlock, roundKeys[0]);
        }


        return oneBlock;
    }



    private byte[] subBytes(byte[] oneBlock) {

        if (sBox == null) {
            sBox = generatorSBoxesAndRcon.getSBox();
        }

        for (int i = 0; i < oneBlock.length; i++) {
            oneBlock[i] = sBox[oneBlock[i]];
        }

        return oneBlock;
    }

    private byte[] reverseSubBytes(byte[] oneBlock) {
        if (invertedSBox == null) {
            invertedSBox = generatorSBoxesAndRcon.getSBox();
        }

        for (int i = 0; i < oneBlock.length; i++) {
            oneBlock[i] = invertedSBox[oneBlock[i]];
        }
        return oneBlock;
    }



    private void addRoundKey(byte[] a, byte[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays have different lengths");
        }
        for (int i = 0; i < a.length; ++i) {
            a[i] = (byte) (a[i] ^ b[i]);
        }
    }

    private void shiftRows(byte[] oneBlock) {
        byte[] temp = Arrays.copyOf(oneBlock, oneBlock.length);

        for (int row = 1; row < 4; row++) {
            for (int col = 0; col < nb; col++) {
                int newPos = (col - row + nb) % nb;
                oneBlock[row + 4 * newPos] = temp[row + 4 * col];
            }
        }
    }

    private void invShiftRows(byte[] oneBlock) {
        byte[] temp = Arrays.copyOf(oneBlock, oneBlock.length);


        for (int row = 1; row < 4; row++) {
            for (int col = 0; col < nb; col++) {
                int newPos = (col + row) % nb;
                oneBlock[row + 4 * newPos] = temp[row + 4 * col];
            }
        }
    }







}
