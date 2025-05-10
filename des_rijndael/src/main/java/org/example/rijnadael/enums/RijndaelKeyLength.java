package org.example.rijnadael.enums;

public enum RijndaelKeyLength {
    KEY_128(4),
    KEY_192(6),
    KEY_256(8);


    private final int amountOf4Bytes;

    RijndaelKeyLength(int amountOf4Bytes) {
        this.amountOf4Bytes = amountOf4Bytes;
    }

    public int getAmountOf4Bytes() {
        return amountOf4Bytes;
    }
}
