package org.example.des;

import lombok.extern.slf4j.Slf4j;
import org.example.interfaces.EncryptionTransformation;
import org.example.interfaces.KeyExpansion;
import org.example.interfaces.impl.FeistelNet;

@Slf4j
public class Des extends FeistelNet {


    public Des(byte[] key, KeyExpansion keyExpansion, EncryptionTransformation encryptionTransformation) {
        super(keyExpansion, encryptionTransformation);
        this.setKey(key);
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
