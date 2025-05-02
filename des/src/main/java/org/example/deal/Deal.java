package org.example.deal;

import org.example.constants.BitsInKeysOfDeal;
import org.example.des.Des;
import org.example.interfaces.impl.FeistelNet;
import org.example.interfaces.impl.FiestelFunction;
import org.example.interfaces.impl.KeyExpansionImpl;
import org.example.utils.Pair;


public class Deal extends FeistelNet {

    public Deal(BitsInKeysOfDeal bits, byte[] keyDes, byte[] keyDeal) {
        super(
                new DealKeyExpansionImpl(bits, new Des(keyDes, new KeyExpansionImpl(), new FiestelFunction())),
                new DealTransformationImpl()
        );
        this.setKey(keyDeal);
    }

    @Override
    public int getBlockSize() {
        return 16;
    }

    @Override
    public byte[] encryptDecryptInner(byte[] oneBlock, byte[] key, boolean isEncrypt) {

        byte[][] roundKeys = this.keyExpansion.generateRoundKeys(key);


        byte[] l0 = new byte[8];
        byte[] r0 = new byte[8];

        System.arraycopy(oneBlock, 0, l0, 0, 8);
        System.arraycopy(oneBlock, 8, r0, 0, 8);


        Pair<byte[], byte[]> l6r6 = roundsFiestelNet(l0, r0, roundKeys, isEncrypt);

        byte[] l16 = l6r6.second();
        byte[] r16 = l6r6.first();

        byte[] preCipherBlock = new byte[16];

        System.arraycopy(l16, 0, preCipherBlock, 0, 8);
        System.arraycopy(r16, 0, preCipherBlock, 8, 8);


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
