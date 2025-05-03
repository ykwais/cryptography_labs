package org.example.des;

import lombok.extern.slf4j.Slf4j;
import org.example.interfaces.EncryptionTransformation;
import org.example.interfaces.KeyExpansion;
import org.example.interfaces.impl.FeistelNet;

@Slf4j
public class Des extends FeistelNet {


    // засунуть внутрь des расширение и преобразование
    public Des(byte[] key, KeyExpansion keyExpansion, EncryptionTransformation encryptionTransformation) {
        super(keyExpansion, encryptionTransformation);
        this.setKey(key);
    }

    @Override
    public int getBlockSize() {
        return 8;
    }


}
