package org.example.magenta;

import org.example.interfaces.EncryptorDecryptorSymmetric;
import org.example.interfaces.KeyExpansion;
import org.example.magenta.enums.MagentaKeyLength;
import org.example.magenta.supply.GeneratorSBlock;

public class Magenta implements EncryptorDecryptorSymmetric {

    private static final int BLOCK_SIZE = 16;
    private byte[] key = null;
    private final KeyExpansion keyExpansion;
    private byte[][] roundKeys = null;
    private byte[] sBlock = null;
    private final GeneratorSBlock generatorSBlock = new GeneratorSBlock((byte) 0x65);

    public Magenta(MagentaKeyLength keyLength, byte[] key) {
        keyExpansion = new MagentaKeyExpansion();
        if (!checkAmountBytesInKey(key, keyLength)) {
            throw new IllegalArgumentException("Key is not a valid RC6 key");
        }
        setKey(key);
        sBlock = generatorSBlock.getSBlock();
    }

    @Override
    public void setKey(byte[] symmetricKey) {
        if (key != null && key.length != symmetricKey.length) {
            throw new IllegalArgumentException("Key length does not match symmetric key length");
        }
        this.key = symmetricKey;
        roundKeys = this.keyExpansion.generateRoundKeys(key);
    }

    private boolean checkAmountBytesInKey(byte[] key, MagentaKeyLength keyLength) {
        return key.length == keyLength.getKeyLengthInBytes();
    }

    @Override
    public byte[] encrypt(byte[] message) {
        return new byte[0];
    }

    @Override
    public byte[] decrypt(byte[] cipherText) {
        return new byte[0];
    }

    @Override
    public int getBlockSize() {
        return BLOCK_SIZE;
    }


    private byte f(byte x) {
        return this.sBlock[Byte.toUnsignedInt(x)];
    }

    private byte a(byte x, byte y) {
        return f((byte) ( x ^ f(y) ) );
    }

    private byte[] pe(byte x, byte y) {
        byte[] result = new byte[2];
        result[0] = a(x,y);
        result[1] = a(y,x);
        return result;
    }

    private void pi(byte[] oneBlock) {
        byte[] clone = oneBlock.clone();
        for (int i = 0; i < 8; i++){
            byte[] resultPE = pe(clone[i], clone[i+8]);
            oneBlock[i] = resultPE[0];
            oneBlock[i+1] = resultPE[1];
        }
    }

    private void t(byte[] oneBlock) {
        for (int i = 0; i < 4; i++){
            pi(oneBlock);
        }
    }

    private byte[] evenByteArray(byte[] oneBlock) {
        byte[] even = new byte[8];
        for(int i=0,j = 0; i < 16; i += 2, j++) {
            even[j] = oneBlock[i];
        }
        return even;
    }

    private byte[] oddByteArray(byte[] oneBlock) {
        byte[] odd = new byte[8];
        for(int i=1,j = 0; i < 16; i += 2, j++) {
            odd[j] = oneBlock[i];
        }
        return odd;
    }










}
