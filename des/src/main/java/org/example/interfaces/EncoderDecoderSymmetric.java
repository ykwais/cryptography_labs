package org.example.interfaces;

public interface EncoderDecoderSymmetric {
    void setKey(byte[] symmetricKey);

    byte[] encode(byte[] message);

    byte[] decode(byte[] cipherText);
}
