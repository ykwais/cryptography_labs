package org.example.interfaces;

public interface EncryptionTransformation {
    byte[] transform(byte[] input, byte[] roundKey);
}
