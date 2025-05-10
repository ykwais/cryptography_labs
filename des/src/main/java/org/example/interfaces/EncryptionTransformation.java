package org.example.interfaces;

public interface EncryptionTransformation {
    byte[] doFunction(byte[] input, byte[] roundKey);
}
