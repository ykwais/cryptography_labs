package org.example.interfaces;

public interface EncryptionTransformation {
    int doFunction(int input, byte[] roundKey);
}
