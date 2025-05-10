package org.example.rijnadael.enums;

public enum RijndaelBlockLength {
    BLOCK_128(4),
    BLOCK_192(6),
    BLOCK_256(8);

    private final int amountOf4Bytes;

    RijndaelBlockLength(int amountOf4Bytes) {
        this.amountOf4Bytes = amountOf4Bytes;
    }

    public int getAmountOf4Bytes() {
        return amountOf4Bytes;
    }


}
