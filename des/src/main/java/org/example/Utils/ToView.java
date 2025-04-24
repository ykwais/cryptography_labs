package org.example.Utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ToView {
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X ", b));
        }
        return hexString.toString().trim();
    }

    public static String intToHex(int value) {
        return String.format("%08X", value);
    }
}
