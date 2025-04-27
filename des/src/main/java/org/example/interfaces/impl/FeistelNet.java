package org.example.interfaces.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.constants.Tables;
import org.example.interfaces.EncryptorDecryptorSymmetric;
import org.example.interfaces.EncryptionTransformation;
import org.example.interfaces.KeyExpansion;
import org.example.utils.Pair;
import org.example.utils.PermutationBits;


import static org.example.utils.ToView.bytesToHex;


@Slf4j
public class FeistelNet implements EncryptorDecryptorSymmetric {

    private byte[] key;
    private final KeyExpansion keyExpansion;
    private final EncryptionTransformation transformation;

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

    public byte[] encryptDecryptInner(byte[] oneBlock, byte[] key, boolean isEncrypt) {

        byte[][] roundKeys = keyExpansion.generateRoundKeys(key);

        log.info("oneBlock hex: {}", bytesToHex(oneBlock));

        byte[] l0r0 = PermutationBits.permute(oneBlock, Tables.IP, true, true);

        log.info("after IP hex: {}", bytesToHex(l0r0));

        byte[] l0 = new byte[4];
        byte[] r0 = new byte[4];

        System.arraycopy(l0r0, 0, l0, 0, 4);
        System.arraycopy(l0r0, 4, r0, 0, 4);
        log.info("l0 HEX: {}", bytesToHex(l0));
        log.info("r0 HEX: {}", bytesToHex(r0));

        Pair<byte[], byte[]> l16r16 = rounds16OfFiestelNet(l0, r0, roundKeys, isEncrypt);

        byte[] l16 = l16r16.second();//переворот здесь!!!!!!!!!!!
        byte[] r16 = l16r16.first();

        byte[] preCipherBlock = new byte[8];

        System.arraycopy(l16, 0, preCipherBlock, 0, 4);
        System.arraycopy(r16, 0, preCipherBlock, 4, 4);


        log.info("l16r16: {}", bytesToHex(preCipherBlock));

        byte[] cipherBlock = PermutationBits.permute(preCipherBlock, Tables.IP_INV, true, true);

        log.info("cipherBlock hex: {}", bytesToHex(cipherBlock));

        return cipherBlock;
    }

    private Pair<byte[], byte[]> rounds16OfFiestelNet(byte[] l0, byte[] r0, byte[][] roundKeys, boolean isEncrypt) {

        byte[] l = l0;
        byte[] r = r0;

        for (int i = 0; i < 16; ++i) {

            log.info("ROUND: {}", i+1);

            byte[] lNext = r;

            int indexOfRoundKey = isEncrypt ? i : roundKeys.length - 1 - i;

            byte[] rNext = xorByteArrays(l ,transformation.doFunction(r, roundKeys[indexOfRoundKey]));

            l = lNext;
            r = rNext;

            log.info("L next: {}", bytesToHex(l));
            log.info("R next: {}", bytesToHex(r));

        }

        log.info("L last: {}", bytesToHex(l));
        log.info("R last: {}", bytesToHex(r));

        return new Pair<>(l,r);
    }

    private byte[] xorByteArrays(byte[] a, byte[] b) {
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
