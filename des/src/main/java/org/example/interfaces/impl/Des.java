package org.example.interfaces.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.constants.Tables;
import org.example.fiestel.FiestelNet;
import org.example.interfaces.EncoderDecoderSymmetric;
import org.example.interfaces.KeyExpansion;
import org.example.utils.Pair;
import org.example.utils.PermutationBits;

import static org.example.utils.ToView.bytesToHex;
import static org.example.utils.ToView.intToHex;

@Slf4j
public class Des implements EncoderDecoderSymmetric {

    private byte[] key;

    public Des() {}

    public Des(byte[] key) {
        this.key = key;
    }

    @Override
    public void setKey(byte[] Key) {
        this.key = Key;
    }

    @Override
    public byte[] encode(byte[] oneBlock) {
        return encodeInner(oneBlock, key, true);
    }

    @Override
    public byte[] decode(byte[] oneBlock) {
        return encodeInner(oneBlock, key, false);
    }

    public byte[] encodeInner(byte[] oneBlock, byte[] key, boolean isEncrypt) {

        FiestelNet net = new FiestelNet();

        KeyExpansion keyExpansion = new KeyExpansionImpl();

        byte[][] roundKeys = keyExpansion.generateRoundKeys(key);

        log.info("oneBlock hex: {}", bytesToHex(oneBlock));

        byte[] l0r0 = PermutationBits.permute(oneBlock, Tables.IP, true, true);

        log.info("after IP hex: {}", bytesToHex(l0r0));

        int l0 = 0;
        int r0 = 0;

        for (int i = 0; i < 4; ++i) {
            byte oneByte = l0r0[i];
            l0 |= oneByte & 0xFF;
            l0 <<= i == 3 ? 0 : 8;
        }

        for (int i = 3; i < 8; ++i) {
            byte oneByte = l0r0[i];
            r0 |= oneByte & 0xFF;
            r0 <<= i == 7 ? 0 : 8;
        }

        log.info("l0 HEX: {}", intToHex(l0));
        log.info("r0 HEX: {}", intToHex(r0));

        Pair<Integer, Integer> l16r16 = net.rounds16OfFiestelNet(l0, r0, roundKeys, isEncrypt);

        int l16 = l16r16.second();//переворот здесь!!!!!!!!!!!
        int r16 = l16r16.first();

        byte[] preCipherBlock = new byte[8];

        for (int k = 3; k >= 0; k-- ) {
            preCipherBlock[k] = (byte) (l16 & 0xFF);
            l16 = l16 >> 8;
        }

        for (int k = 7; k>3; k--) {
            preCipherBlock[k] = (byte) (r16 & 0xFF);
            r16 = r16 >> 8;
        }

        log.info("l16r16: {}", bytesToHex(preCipherBlock));

        byte[] cipherBlock = PermutationBits.permute(preCipherBlock, Tables.IP_INV, true, true);

        log.info("cipherBlock hex: {}", bytesToHex(cipherBlock));

        return cipherBlock;
    }

    public static byte[] addPkcs7Padding(byte[] data) {
        int padLength = 8 - (data.length % 8);
        byte[] padded = new byte[data.length + padLength];
        System.arraycopy(data, 0, padded, 0, data.length);
        for (int i = data.length; i < padded.length; i++) {
            padded[i] = (byte) padLength;
        }
        return padded;
    }

    public static byte[] removePkcs7Padding(byte[] data) {
        int padLength = data[data.length - 1];
        byte[] unpadded = new byte[data.length - padLength];
        System.arraycopy(data, 0, unpadded, 0, unpadded.length);
        return unpadded;
    }
}
