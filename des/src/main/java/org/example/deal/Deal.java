package org.example.deal;

import org.example.constants.BitsInKeysOfDeal;
import org.example.des.Des;
import org.example.interfaces.impl.FeistelNet;
import org.example.utils.Pair;


public class Deal extends FeistelNet {

    private final int blockSize = 16;

    public Deal(BitsInKeysOfDeal bits, byte[] keyDes, byte[] keyDeal) {
        super(
                new DealKeyExpansionImpl(bits, new Des(keyDes)),
                new DealTransformationImpl()
        );
        if (!isCorrectAmountBits(bits, keyDeal)) {
            throw new IllegalArgumentException("The key deal's amount is incorrect.");
        }
        this.setKey(keyDeal);
    }

    private boolean isCorrectAmountBits(BitsInKeysOfDeal bitsInKeys, byte[] keyDeal) {
        int amountBytes = 0;
        switch (bitsInKeys) {
            case BIT_128 -> amountBytes = 16;
            case BIT_256 -> amountBytes = 32;
            case BIT_192 -> amountBytes = 24;
            default -> throw new IllegalArgumentException("invalid bits in key deal");
        }
        return keyDeal.length == amountBytes;
    }

    @Override
    public int getBlockSize() {
        return blockSize;
    }

    @Override
    public byte[] encryptDecryptInner(byte[] oneBlock, byte[] key, boolean isEncrypt) {

        byte[][] roundKeys = this.keyExpansion.generateRoundKeys(key);

        int halfOfBlockSize = blockSize / 2;

        byte[] l0 = new byte[halfOfBlockSize];
        byte[] r0 = new byte[halfOfBlockSize];

        System.arraycopy(oneBlock, 0, l0, 0, halfOfBlockSize);
        System.arraycopy(oneBlock, halfOfBlockSize, r0, 0, halfOfBlockSize);


        Pair<byte[], byte[]> l6r6 = roundsFiestelNet(l0, r0, roundKeys, isEncrypt);

        byte[] l16 = l6r6.second();
        byte[] r16 = l6r6.first();

        byte[] preCipherBlock = new byte[blockSize];

        System.arraycopy(l16, 0, preCipherBlock, 0, halfOfBlockSize);
        System.arraycopy(r16, 0, preCipherBlock, halfOfBlockSize, halfOfBlockSize);


        return preCipherBlock;
    }

    private Pair<byte[], byte[]> roundsFiestelNet(byte[] l0, byte[] r0, byte[][] roundKeys, boolean isEncrypt) {

        byte[] l = l0;
        byte[] r = r0;

        for (int i = 0; i < roundKeys.length; ++i) {

            int indexOfRoundKey = isEncrypt ? i : roundKeys.length - 1 - i;

            byte[] rNext = xorByteArrays(r ,this.transformation.doFunction(l, roundKeys[indexOfRoundKey]));

            r = l;
            l = rNext;


        }

        return new Pair<>(l,r);
    }


}
