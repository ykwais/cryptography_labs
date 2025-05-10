package org.example.deal;

import org.example.constants.BitsInKeysOfDeal;
import org.example.des.Des;
import org.example.interfaces.KeyExpansion;

import java.util.Arrays;

public class DealKeyExpansionImpl implements KeyExpansion {
    private final Des des;
    private final BitsInKeysOfDeal bitsInKeysOfDeal;

    DealKeyExpansionImpl(BitsInKeysOfDeal bitsInKeysOfDeal, Des des) {
        this.bitsInKeysOfDeal = bitsInKeysOfDeal;
        this.des = des;
    }

    @Override
    public byte[][] generateRoundKeys(byte[] keyLarge) {
        int amountDefaultKeys = bitsInKeysOfDeal.getAmountDefaultKeys();
        if (keyLarge.length != amountDefaultKeys * 8) {
            throw new IllegalArgumentException("Key large must be " + amountDefaultKeys * 8 + " bytes");
        }

        byte[][] separatedKeys = new byte[amountDefaultKeys][];
        for (int i = 0; i < amountDefaultKeys; i++) {
            separatedKeys[i] = Arrays.copyOfRange(keyLarge, i*8, 8 * (i + 1));
        }

        int amountRoundsForThisTypeKey = bitsInKeysOfDeal.getAmountRounds();

        byte[][] roundKeys = new byte[amountRoundsForThisTypeKey][];

        roundKeys[0] = des.encrypt(separatedKeys[0]);
        roundKeys[1] = des.encrypt(xorByteArrays(roundKeys[0], separatedKeys[1]));


        switch(amountDefaultKeys) {
            case 2 :
                for (int i = 2; i < amountRoundsForThisTypeKey; i++) {
                    long shift = 1L << (64 - (1 << (i - 2)));
                    byte[] shiftedOne = longToBytes(shift);
                    roundKeys[i] = des.encrypt(xorByteArrays(separatedKeys[i % amountDefaultKeys], xorByteArrays(shiftedOne, roundKeys[i-1])));
                }
                break;
            case 3 :
                roundKeys[2] = des.encrypt(xorByteArrays(separatedKeys[0], roundKeys[1]));
                for(int i = 3; i < amountRoundsForThisTypeKey-1; i++) {
                    long shift = 1L << (64 - (1 << ((i-1) - 2)));
                    byte[] shiftedOne = longToBytes(shift);
                    roundKeys[i] = des.encrypt(xorByteArrays(separatedKeys[i % 2],xorByteArrays(shiftedOne, roundKeys[i-1])));
                }
                roundKeys[5] = des.encrypt(xorByteArrays(separatedKeys[2], xorByteArrays(longToBytes(1L << (64 - (1 << 2))), roundKeys[4])));
                break;
            case 4:
                roundKeys[2] = des.encrypt(xorByteArrays(separatedKeys[2], roundKeys[1]));
                roundKeys[3] = des.encrypt(xorByteArrays(separatedKeys[3], roundKeys[2]));

                for(int i = 4; i < amountRoundsForThisTypeKey-1; i++) {
                    long shift = 1L << (64 - (1 << ((i-2) - 2)));
                    byte[] shiftedOne = longToBytes(shift);
                    roundKeys[i] = des.encrypt(xorByteArrays(separatedKeys[i % 4],xorByteArrays(shiftedOne, roundKeys[i-1])));
                }

                roundKeys[7] = des.encrypt(xorByteArrays(separatedKeys[3], xorByteArrays(longToBytes(1L << (64 - (1 << 3))), roundKeys[5])));

                break;
            default:
                throw new IllegalArgumentException("Amount of default keys must be either 2 or 3 or 4");
        }
        return roundKeys;
    }

    private byte[] longToBytes(long x) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (x & 0xFF);
            x >>= 8;
        }
        return result;
    }

    private byte[] xorByteArrays(byte[] a, byte[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays have different lengths");
        }
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; ++i) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }
}
