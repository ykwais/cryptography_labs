package org.example.rijnadael;

import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import org.example.interfaces.impl.FeistelNet;
import org.example.rijnadael.enums.RijndaelBlockLength;
import org.example.rijnadael.enums.RijndaelKeyLength;
import org.example.rijnadael.supply.GeneratorSBoxes;

import static org.example.rijnadael.stateless.GaloisOperations.getInversePolynom;

public class Rijndael extends FeistelNet {
    private final int blockSize;
    private byte[] sBox = null;
    private byte[] invertedSBox = null;
    private GeneratorSBoxes generatorSBoxes = new GeneratorSBoxes();

    @Getter
    private byte polynomeIrr = 0x1B;

    public Rijndael(RijndaelKeyLength keyLength, RijndaelBlockLength blockLength, byte[] key) {
        super( new RijndaelKeyExpansionImpl(keyLength, blockLength), new RijndaelTransformationImpl());
        blockSize = blockLength.getAmountOf4Bytes() * 4;
        if(!checkAmountBytesInKey(key, keyLength)) {
            throw new IllegalArgumentException("Amount of bytes in key does not match with your parameters");
        }
        this.setKey(key);
    }

    public void setPolynomeIrr(byte polynomeIrr) {
        this.polynomeIrr = polynomeIrr;
        sBox = null;
        invertedSBox = null;
    }

    @Override
    public int getBlockSize() {
        return blockSize;
    }

    private boolean checkAmountBytesInKey(byte[] key, RijndaelKeyLength keyLength) {
        return key.length == keyLength.getAmountOf4Bytes() * 4;
    }

    @Override
    public byte[] encryptDecryptInner(byte[] oneBlock, byte[] key, boolean isEncrypt) {

        //byte[][] roundKeys = this.keyExpansion.generateRoundKeys(key);

        if(isEncrypt) {
            //add round key
            // цикл
            // final round
        } else {
            // inverted final round
            // reverse cycle
            // remove round key
        }


        return new byte[0];
    }



    private byte[] subBytes(byte[] oneBlock) {

        if (sBox == null) {
            sBox = generatorSBoxes.getSBox(polynomeIrr);
        }

        for (int i = 0; i < oneBlock.length; i++) {
            oneBlock[i] = sBox[oneBlock[i]];
        }

        return oneBlock;
    }

    private byte[] reverseSubBytes(byte[] oneBlock) {
        if (invertedSBox == null) {
            invertedSBox = generatorSBoxes.getSBox(polynomeIrr);
        }

        for (int i = 0; i < oneBlock.length; i++) {
            oneBlock[i] = invertedSBox[oneBlock[i]];
        }
        return oneBlock;
    }




}
