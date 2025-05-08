package org.example.deal;

import org.example.des.Des;
import org.example.interfaces.EncryptionTransformation;


public class DealTransformationImpl implements EncryptionTransformation {

    @Override
    public byte[] doFunction(byte[] input, byte[] roundKey) {

        Des des = new Des(roundKey);

        return des.encrypt(input);
    }
}
