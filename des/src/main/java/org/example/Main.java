package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.interfaces.KeyExpansion;
import org.example.interfaces.impl.KeyExpansionImpl;

@Slf4j
public class Main {
    public static void main(String[] args) {

        byte[] key = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xC4, (byte)0xC8, (byte)0xC0, (byte)0xCD, (byte)0xC0};
        //byte[] key = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};

//        byte test = (byte)0xC0;
//        log.info("BIN: {}",Integer.toBinaryString(test & 0xFF));
//
//        log.info("BIN: {}",Integer.toBinaryString(((test << 2) >> 7) & 1));

        KeyExpansion keyExpansion = new KeyExpansionImpl();

        byte[][] res = keyExpansion.generateRoundKeys(key);

    }
}




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