package org.example.interfaces.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.constants.Tables;
import org.example.interfaces.EncryptorDecryptorSymmetric;
import org.example.interfaces.EncryptionTransformation;
import org.example.interfaces.KeyExpansion;
import org.example.utils.Pair;
import org.example.utils.PermutationBits;


@Slf4j
public class FeistelNet implements EncryptorDecryptorSymmetric {

    private byte[] key;
    protected final KeyExpansion keyExpansion;
    protected final EncryptionTransformation transformation;

    public FeistelNet(KeyExpansion keyExpansion, EncryptionTransformation transformation) {
        this.keyExpansion = keyExpansion;
        this.transformation = transformation;
    }


    @Override
    public void setKey(byte[] key) {
        this.key = key;
    }

    @Override
    public byte[] encrypt(byte[] oneBlock) {
        return encryptDecryptInner(oneBlock, key, true);
    }

    @Override
    public byte[] decrypt(byte[] oneBlock) {
        return encryptDecryptInner(oneBlock, key, false);
    }

    @Override
    public int getBlockSize() {
        return 0;
    }

    public byte[] encryptDecryptInner(byte[] oneBlock, byte[] key, boolean isEncrypt) {

        byte[][] roundKeys = keyExpansion.generateRoundKeys(key);


        byte[] l0r0 = PermutationBits.permute(oneBlock, Tables.IP, true, true);

        int currentBlockSize = getBlockSize();
        int halfOfCurrentBlockSize = currentBlockSize / 2;

        byte[] l0 = new byte[halfOfCurrentBlockSize];
        byte[] r0 = new byte[halfOfCurrentBlockSize];

        System.arraycopy(l0r0, 0, l0, 0, halfOfCurrentBlockSize);
        System.arraycopy(l0r0, halfOfCurrentBlockSize, r0, 0, halfOfCurrentBlockSize);

        Pair<byte[], byte[]> l16r16 = rounds16OfFiestelNet(l0, r0, roundKeys, isEncrypt);

        byte[] l16 = l16r16.second();//переворот здесь!!!!!!!!!!!
        byte[] r16 = l16r16.first();

        byte[] preCipherBlock = new byte[currentBlockSize];

        System.arraycopy(l16, 0, preCipherBlock, 0, halfOfCurrentBlockSize);
        System.arraycopy(r16, 0, preCipherBlock, halfOfCurrentBlockSize, halfOfCurrentBlockSize);


        return PermutationBits.permute(preCipherBlock, Tables.IP_INV, true, true);
    }

    protected Pair<byte[], byte[]> rounds16OfFiestelNet(byte[] l0, byte[] r0, byte[][] roundKeys, boolean isEncrypt) {

        byte[] l = l0;
        byte[] r = r0;

        for (int i = 0; i < roundKeys.length; ++i) {

            byte[] lNext = r;

            int indexOfRoundKey = isEncrypt ? i : roundKeys.length - 1 - i;

            byte[] rNext = xorByteArrays(l ,transformation.doFunction(r, roundKeys[indexOfRoundKey]));

            l = lNext;
            r = rNext;

        }
        return new Pair<>(l,r);
    }

    protected byte[] xorByteArrays(byte[] a, byte[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays have different lengths");
        }
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; ++i) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }
}
