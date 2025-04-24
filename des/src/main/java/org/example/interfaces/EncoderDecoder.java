package org.example.interfaces;

public interface EncoderDecoder {
    void setKey(byte[] symmetricKey);

    byte[] encode(byte[] message);

    byte[] decode(byte[] cipherText);
}
