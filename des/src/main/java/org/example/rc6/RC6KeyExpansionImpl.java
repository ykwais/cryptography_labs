package org.example.rc6;

import lombok.extern.slf4j.Slf4j;
import org.example.interfaces.KeyExpansion;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Slf4j
public class RC6KeyExpansionImpl implements KeyExpansion {

    private final int amountRounds;
    private final int w = 32;
    private final int P = 0xB7E15163;
    private final int Q = 0x9E3779B9;


    public RC6KeyExpansionImpl() {
        this.amountRounds = 20;
    }

    public RC6KeyExpansionImpl(int amountRounds) {
        this.amountRounds = amountRounds;
    }

    @Override
    public byte[][] generateRoundKeys(byte[] key) {

        int b = key.length;
        int c = (b == 0) ? 1 : ((8 * b ) / w);

        int[] L = new int[c];

        ByteBuffer buffer = ByteBuffer.wrap(key);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < L.length; i++) {
            L[i] = buffer.getInt();
        }

        int t = 2 * amountRounds + 4;
        int[] S = new int[t];
        S[0] = P;
        for (int i = 1; i < S.length; i++) {
            S[i] = (int) (Integer.toUnsignedLong(S[i - 1]) + Integer.toUnsignedLong(Q));
        }

        int A = 0;
        int B = 0;
        int i = 0;
        int j = 0;
        int v = Math.max(t, c) * 3;
        for (int k = 0; k < v; k++) {
            A = S[i] = LeftShift((int) (Integer.toUnsignedLong(S[i]) + Integer.toUnsignedLong(A)  + Integer.toUnsignedLong(B)), 3);
            B = L[j] = LeftShift((int) (Integer.toUnsignedLong(L[j]) + Integer.toUnsignedLong(A)  + Integer.toUnsignedLong(B)), (int) (Integer.toUnsignedLong(A)  + Integer.toUnsignedLong(B)));
            i = (int)((Integer.toUnsignedLong(i) + 1) % Integer.toUnsignedLong(t));
            j = (int)((Integer.toUnsignedLong(j) + 1) % Integer.toUnsignedLong(c));
        }

        log.info("s original: {}", S);

        byte[][] result = new byte[S.length][4];
        ByteBuffer bufferTmp = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);

        for (int k = 0; k < S.length; k++) {
            bufferTmp.putInt(S[k]);
            result[k] = bufferTmp.array().clone();
            bufferTmp.clear();
        }

        return result;
    }

    private int LeftShift(int x, int shift) {
        return (x << shift) | ( x >>> (32 - shift));
    }
}
