package org.example.context;

import org.example.constants.PaddingMode;
import org.example.constants.ReadingMode;

public class Context {

    private byte[] key;
    private ReadingMode readingMode;
    private PaddingMode paddingMode;
    private byte[][] roundKeys;

    public Context(byte[] key, ReadingMode readingMode, PaddingMode paddingMode) {
        this.key = key;
        this.readingMode = readingMode;
        this.paddingMode = paddingMode;
    }


}
