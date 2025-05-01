package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.constants.CipherMode;
import org.example.constants.PaddingMode;
import org.example.constants.TypeAlgorithm;
import org.example.context.Context;
import org.example.interfaces.EncryptorDecryptorSymmetric;
import org.example.des.Des;
import org.example.interfaces.impl.FiestelFunction;
import org.example.interfaces.impl.KeyExpansionImpl;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.example.utils.ToView.bytesToHex;

@Slf4j
public class Main {

    public static void main(String[] args) {

        byte[] key = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xC4, (byte)0xC8, (byte)0xC0, (byte)0xCD, (byte)0xC0};
        byte[] keyDeal = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xC4, (byte)0xC8, (byte)0xC0, (byte)0xCD, (byte)0xC0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xC4, (byte)0xC8, (byte)0xC0, (byte)0xCD, (byte)0xC0};
        byte[] initialVector = {(byte)0x01, (byte)0x01, (byte)0x01, (byte)0xC4, (byte)0xC8, (byte)0xC0, (byte)0xCD, (byte)0xC0};

        Context context = new Context(TypeAlgorithm.DES, key, CipherMode.CTR, PaddingMode.ANSI_X923, initialVector, keyDeal, 10);

        byte[] fileData = null;
        try {
            fileData = Files.readAllBytes(Paths.get("8bytes.txt"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        EncryptorDecryptorSymmetric des = new Des(key, new KeyExpansionImpl(), new FiestelFunction());


        byte[] paddedData = context.addPkcs7Padding(fileData);


        byte[] encryptedData = new byte[paddedData.length];
        for (int i = 0; i < paddedData.length; i += 8) {
            byte[] block = new byte[8];
            System.arraycopy(paddedData, i, block, 0, 8);
            byte[] encryptedBlock = des.encrypt(block);
            System.arraycopy(encryptedBlock, 0, encryptedData, i, 8);
        }

        byte[] decryptedData = new byte[encryptedData.length];
        for (int i = 0; i < encryptedData.length; i += 8) {
            byte[] block = new byte[8];
            System.arraycopy(encryptedData, i, block, 0, 8);
            byte[] decryptedBlock = des.decrypt(block);
            System.arraycopy(decryptedBlock, 0, decryptedData, i, 8);
        }


        byte[] originalData = context.removePkcs7Padding(decryptedData);

        log.info("исходный текст в hex: {}", bytesToHex(originalData));



        byte[] res = context.encryptDecryptInner(fileData, true);
        log.info("res: {}", bytesToHex(res));

    }
}
