package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.des.Des;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.example.utils.ToView.bytesToHex;

@Slf4j
public class Main {
    public static void main(String[] args) {

        byte[] key = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xC4, (byte)0xC8, (byte)0xC0, (byte)0xCD, (byte)0xC0};


        byte[] oneBlockOfMessage = null;
        try {

            oneBlockOfMessage = Files.readAllBytes(Paths.get("8bytes.txt"));

            log.info("Размер данных: {}", oneBlockOfMessage.length + " байт");
            log.info("Содержимое: {}", bytesToHex(oneBlockOfMessage));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        Des des = new Des();



        byte[] chipherText = des.encode(oneBlockOfMessage, key);

        log.info("шифроблок: {}", bytesToHex(chipherText));

        byte[] startedText = des.decode(chipherText, key);

        log.info("исходный текст в hex: {}", bytesToHex(startedText));



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