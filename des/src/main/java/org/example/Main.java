package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.interfaces.EncoderDecoderSymmetric;
import org.example.interfaces.impl.Des;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.example.utils.ToView.bytesToHex;

@Slf4j
public class Main {

    public static void main(String[] args) {

        byte[] key = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xC4, (byte)0xC8, (byte)0xC0, (byte)0xCD, (byte)0xC0};

        byte[] fileData = null;
        try {
            fileData = Files.readAllBytes(Paths.get("8bytes.txt"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        EncoderDecoderSymmetric des = new Des(key);

        byte[] paddedData = Des.addPkcs7Padding(fileData);


        byte[] encryptedData = new byte[paddedData.length];
        for (int i = 0; i < paddedData.length; i += 8) {
            byte[] block = new byte[8];
            System.arraycopy(paddedData, i, block, 0, 8);
            byte[] encryptedBlock = des.encode(block);
            System.arraycopy(encryptedBlock, 0, encryptedData, i, 8);
        }

        byte[] decryptedData = new byte[encryptedData.length];
        for (int i = 0; i < encryptedData.length; i += 8) {
            byte[] block = new byte[8];
            System.arraycopy(encryptedData, i, block, 0, 8);
            byte[] decryptedBlock = des.decode(block);
            System.arraycopy(decryptedBlock, 0, decryptedData, i, 8);
        }


        byte[] originalData = Des.removePkcs7Padding(decryptedData);

        log.info("исходный текст в hex: {}", bytesToHex(originalData));


//        byte[] oneBlockOfMessage = null;
//        try {
//
//            oneBlockOfMessage = Files.readAllBytes(Paths.get("8bytes.txt"));
//
//            log.info("Размер данных: {}", oneBlockOfMessage.length + " байт");
//            log.info("Содержимое: {}", bytesToHex(oneBlockOfMessage));
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//        }
//
//        Des des = new Des();
//
//
//
//        byte[] chipherText = des.encode(oneBlockOfMessage, key);
//
//        log.info("шифроблок: {}", bytesToHex(chipherText));
//
//        byte[] startedText = des.decode(chipherText, key);
//
//        log.info("исходный текст в hex: {}", bytesToHex(startedText));



    }
}


//byte[] key = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};

//        byte test = (byte)0xC0;
//        log.info("BIN: {}",Integer.toBinaryString(test & 0xFF));
//
//        log.info("BIN: {}",Integer.toBinaryString(((test << 2) >> 7) & 1));



//byte number = 12;
//
//
//        log.info("HEX: {}", Integer.toHexString(number));
//
//
//        log.info("BIN: {}",Integer.toBinaryString(number));
//
//number >>= 1;
//
//        log.info("BIN: {}",Integer.toBinaryString(number));