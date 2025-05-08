package org.example.rijnadael;

public interface GaloisOperations {
    static byte addPolys(byte a, byte b) {
        return (byte) (a ^ b);
    }

    static byte multOnX(byte number, byte mod) {
        //TODO проверка на неприводимый полином

        if ((number & 0x80) ==  0x80) {
            return (byte) ((number << 1) ^ (mod & 0xFF) );
        }
        return (byte) (number << 1);
    }

    static byte multiplyPolymomsByMod(byte pol, byte a, byte mod) {
        //TODO проверка на неприводимый полином
        byte result = 0;
        for (int i = 0; i < 8; i++) {
            if ((a & 0x01) == 0x01) {
                result ^= pol;
            }
            pol = multOnX(pol, mod);
            a >>= 1;
        }
        return result;
    }

    static int degree(short poly) {
        if (poly == 0) return -1;
        int degree = 0;
        int tmp = poly & 0xFFFF;
        while (tmp != 0) {
            degree++;
            tmp >>>= 1;
        }
        return degree-1;
    }




}
