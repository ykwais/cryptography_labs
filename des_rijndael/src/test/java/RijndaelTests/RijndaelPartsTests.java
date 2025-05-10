package RijndaelTests;

import org.example.rijnadael.Rijndael;
import org.example.rijnadael.enums.RijndaelBlockLength;
import org.example.rijnadael.enums.RijndaelKeyLength;
import org.example.rijnadael.supply.GeneratorSBoxesAndRcon;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RijndaelPartsTests {



    @Test
    void testSBoxInversionCorrectness() {
        GeneratorSBoxesAndRcon generator = new GeneratorSBoxesAndRcon((byte) 0x1B);
        byte[] sBox = generator.getSBox();

        System.out.println("S-Box in HEX format:");
        System.out.println("     0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F");
        System.out.println("   -------------------------------------------------");
        for (int i = 0; i < 16; i++) {
            System.out.printf("%X | ", i);
            for (int j = 0; j < 16; j++) {
                System.out.printf("%02X ", sBox[i * 16 + j] & 0xFF); // Байт в HEX
            }
            System.out.println();
        }

        byte[] invSBox = generator.getInvertedSBox();

        for (int i = 0; i < 256; i++) {
            int s = sBox[i] & 0xFF;
            int restored = invSBox[s] & 0xFF;
            assertEquals(i, restored, "S-box inversion failed at index: " + i);
        }
    }


    @Test
    void testSubBytesAndReverse() {
        Rijndael rijndael = new Rijndael(RijndaelKeyLength.KEY_128, RijndaelBlockLength.BLOCK_128, new byte[16]);
        byte[] original = new byte[16];
        new Random(0).nextBytes(original);

        byte[] encrypted = Arrays.copyOf(original, original.length);
        rijndael.subBytes(encrypted);
        rijndael.reverseSubBytes(encrypted);

        assertArrayEquals(original, encrypted, "SubBytes followed by ReverseSubBytes should return the original block");
    }

    @Test
    void testShiftRowsAndReverse() {
        Rijndael rijndael = new Rijndael(RijndaelKeyLength.KEY_128, RijndaelBlockLength.BLOCK_128, new byte[16]);
        byte[] block = new byte[16];
        for (int i = 0; i < block.length; i++) {
            block[i] = (byte) i;
        }

        byte[] shifted = Arrays.copyOf(block, block.length);
        rijndael.shiftRows(shifted);
        rijndael.reversedShiftRows(shifted);

        assertArrayEquals(block, shifted, "ShiftRows followed by ReversedShiftRows should return the original block");
    }

    @Test
    void testAddRoundKey() {
        Rijndael rijndael = new Rijndael(RijndaelKeyLength.KEY_128, RijndaelBlockLength.BLOCK_128, new byte[16]);
        byte[] a = new byte[16];
        byte[] b = new byte[16];
        for (int i = 0; i < 16; i++) {
            a[i] = (byte) i;
            b[i] = (byte) (255 - i);
        }

        byte[] result = Arrays.copyOf(a, 16);
        rijndael.addRoundKey(result, b);

        for (int i = 0; i < 16; i++) {
            assertEquals((byte)(a[i] ^ b[i]), result[i], "AddRoundKey failed at index " + i);
        }
    }

    @Test
    void testMixColumnsAndInverse() {
        Rijndael rijndael = new Rijndael(RijndaelKeyLength.KEY_128, RijndaelBlockLength.BLOCK_128, new byte[16]);
        byte[] original = new byte[16];
        new Random(1).nextBytes(original);

        byte[] mixed = Arrays.copyOf(original, original.length);
        rijndael.mixColumns(mixed, true);
        rijndael.mixColumns(mixed, false);

        assertArrayEquals(original, mixed, "MixColumns followed by its inverse should return the original block");
    }


    @Test
    void testEncryptDecrypt() {
        byte[] key = new byte[16];
        new Random(1000).nextBytes(key);

        Rijndael rijndael = new Rijndael(RijndaelKeyLength.KEY_128, RijndaelBlockLength.BLOCK_128, key);
        byte[] block = new byte[16];
        new Random(10000).nextBytes(block);


        byte[] encrypted = rijndael.encrypt(Arrays.copyOf(block, 16));
        byte[] decrypted = rijndael.decrypt(encrypted);

        assertArrayEquals(block, decrypted, "Decrypted block should match original after encryption");
    }





}
