package org.example.des;

import lombok.extern.slf4j.Slf4j;
import org.example.interfaces.EncryptionTransformation;
import org.example.interfaces.KeyExpansion;
import org.example.interfaces.impl.FeistelNet;
import org.example.interfaces.impl.FiestelFunction;
import org.example.interfaces.impl.KeyExpansionImpl;

@Slf4j
public class Des extends FeistelNet {

    private final int blockSize = 8;

    public Des(byte[] key) {
        super(new KeyExpansionImpl(), new FiestelFunction());
        this.setKey(key);
    }

    @Override
    public int getBlockSize() {
        return blockSize;
    }

}
