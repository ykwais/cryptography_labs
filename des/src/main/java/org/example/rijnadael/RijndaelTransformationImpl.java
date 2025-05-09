package org.example.rijnadael;

import org.example.interfaces.EncryptionTransformation;

public class RijndaelTransformationImpl implements EncryptionTransformation {

    @Override
    public byte[] doFunction(byte[] input, byte[] roundKey) {
        return new byte[0];
    }

}
