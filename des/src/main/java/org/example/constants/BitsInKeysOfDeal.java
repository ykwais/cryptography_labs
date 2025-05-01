package org.example.constants;

public enum BitsInKeysOfDeal {
    BIT_128(2),
    BIT_192(3),
    BIT_256(4);

    private final int amountDefaultKeys;

    BitsInKeysOfDeal(int amountDefaultKeys) {
        this.amountDefaultKeys = amountDefaultKeys;
    }

    public int getAmountDefaultKeys() {
        return amountDefaultKeys;
    }

    public int getAmountRounds() {
        switch(this) {
            case BIT_128, BIT_192 -> {return 6;}
            case BIT_256 -> {return 8;}
            default -> throw new UnsupportedOperationException("Unexpected value: " + this);
        }
    }
}
