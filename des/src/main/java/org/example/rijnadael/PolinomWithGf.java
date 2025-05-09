package org.example.rijnadael;

import static org.example.rijnadael.stateless.GaloisOperations.addPolys;
import static org.example.rijnadael.stateless.GaloisOperations.multiplyPolymomsByMod;

public record PolinomWithGf(byte d3, byte d2, byte d1, byte d0) {

    public static PolinomWithGf add(PolinomWithGf a, PolinomWithGf b) {
        return new PolinomWithGf(addPolys(a.d3(), b.d3()), addPolys(a.d2(), b.d2()), addPolys(a.d1(), b.d1()), addPolys(a.d0(), b.d0()));
    }

    public static PolinomWithGf mult(PolinomWithGf a, PolinomWithGf b, byte mod) {
        byte d3 = (byte) (multiplyPolymomsByMod(a.d3(), b.d0(), mod) ^ multiplyPolymomsByMod(a.d2(), b.d1(), mod) ^ multiplyPolymomsByMod(a.d1(), b.d2(), mod) ^ multiplyPolymomsByMod(a.d0(), b.d3(), mod));
        byte d2 = (byte) (multiplyPolymomsByMod(a.d2(), b.d0(), mod) ^ multiplyPolymomsByMod(a.d1(), b.d1(), mod) ^ multiplyPolymomsByMod(a.d0(), b.d2(), mod) ^ multiplyPolymomsByMod(a.d3(), b.d3(), mod));
        byte d1 = (byte) (multiplyPolymomsByMod(a.d1(), b.d0(), mod) ^ multiplyPolymomsByMod(a.d0(), b.d1(), mod) ^ multiplyPolymomsByMod(a.d3(), b.d2(), mod) ^ multiplyPolymomsByMod(a.d2(), b.d3(), mod));
        byte d0 = (byte) (multiplyPolymomsByMod(a.d0(), b.d0(), mod) ^ multiplyPolymomsByMod(a.d3(), b.d1(), mod) ^ multiplyPolymomsByMod(a.d2(), b.d2(), mod) ^ multiplyPolymomsByMod(a.d1(), b.d3(), mod));
        return new PolinomWithGf(d3, d2, d1, d0);
    }
}
