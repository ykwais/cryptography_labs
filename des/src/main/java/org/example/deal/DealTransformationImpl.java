package org.example.deal;

import org.example.des.Des;
import org.example.interfaces.EncryptionTransformation;
import org.example.interfaces.impl.FiestelFunction;
import org.example.interfaces.impl.KeyExpansionImpl;

public class DealTransformationImpl implements EncryptionTransformation {

    @Override
    public byte[] doFunction(byte[] input, byte[] roundKey) {

        Des des = new Des(roundKey, new KeyExpansionImpl(), new FiestelFunction());

        return des.encrypt(input);
    }
}
