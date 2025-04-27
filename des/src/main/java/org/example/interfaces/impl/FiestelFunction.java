package org.example.interfaces.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.constants.Tables;
import org.example.interfaces.EncryptionTransformation;
import org.example.utils.PermutationBits;

import static org.example.utils.ToView.*;

@Slf4j
public class FiestelFunction implements EncryptionTransformation { // шифрующее преобразование //TODO: изменить на массив байтов

    @Override
    public int doFunction(int right, byte[] roundKey) {

        log.info("bin of right part in FiestelFunction: {}", intToHex(right));

        byte[] rightInBteArray = new byte[4];

        for (int i = 3; i >= 0; i--) {
            rightInBteArray[i] = (byte) (right & 0xFF);
            right >>>= 8;
        }

        log.info("hex of bytes array of right part: {}", bytesToHex(rightInBteArray));

        byte[] rightAfterExpansion = PermutationBits.permute(rightInBteArray, Tables.E, true, true);

        log.info("hex of bytes array after expansion of right part: {}", bytesToHex(rightAfterExpansion));

        log.info("hex of ROUND KEY                                : {}", bytesToHex(roundKey));

        log.info("bin right after expansion: {}", bytesToBinary(rightAfterExpansion));

        log.info("bin after expan ROUND KEY: {}", bytesToBinary(roundKey));

        byte[] afterXor = xorByteArrays(rightAfterExpansion, roundKey);

        log.info("bin after xor            : {}", bytesToBinary(afterXor));

        log.info("hex after xor: {}", bytesToHex(afterXor));


        long forDivisionOn6Bit = 0;

        for (int i = 0; i < 6; i++) {
            byte oneByte = afterXor[i];
            forDivisionOn6Bit |= oneByte & 0xFF;
            forDivisionOn6Bit <<= i == 5 ? 0 : 8;
        }

        byte[] bytesBy6Bit = new byte[8];

        for (int i = 7; i >= 0; --i) {
            bytesBy6Bit[i] = (byte) (forDivisionOn6Bit & 0x3F);
            forDivisionOn6Bit >>>= 6;
        }



        // на данном этапе имеется массив размера 8 на каждые 6 бит

        int preResult = 0;

        for (int i = 0; i < 8; ++i) {
            byte oneByte = bytesBy6Bit[i];
            log.info("oneByte in hex: {}", String.format("%02X ", oneByte));
            int row = (((oneByte & 0x20) >>> 4) | (oneByte & 0x01) ) & 0xFF;
            int col = ((oneByte & 0x1E) >>> 1) & 0xFF;

            preResult |= (Tables.S[i][row][col]) & 0x0F;
            preResult <<= i == 7 ? 0 : 4;
        }

        log.info("after S transform: {}", intToHex(preResult));

        byte[] intToByteArrayForPPermutation = new byte[4];


        for (int i = 3; i >= 0; i--) {
            intToByteArrayForPPermutation[i] = (byte) (preResult & 0xFF);
            preResult >>>= 8;
        }

        log.info("after S transform hex: {}", bytesToHex(intToByteArrayForPPermutation));


        byte[] prePreResult = PermutationBits.permute(intToByteArrayForPPermutation, Tables.P, true, true);

        log.info("after P transform hex: {}", bytesToHex(prePreResult));

        int result = 0;

        for (int i = 0; i < 4; i++) {
            result |= prePreResult[i] & 0xFF;
            result <<= i == 3 ? 0 : 8;
        }

        log.info("after P transform: {}", intToHex(result));

        return result;
    }

    private byte[] xorByteArrays(byte[] a, byte[] b) {

        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays have different lengths");
        }

        byte[] c = new byte[a.length];
        for (int i = 0; i < c.length; i++) {
            c[i] = (byte) (a[i] ^ b[i]);
        }
        return c;
    }
}
