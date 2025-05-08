import org.example.constants.CipherMode;
import org.example.constants.PaddingMode;
import org.example.constants.TypeAlgorithm;
import org.example.context.Context;
import org.example.des.Des;
import org.example.interfaces.impl.FiestelFunction;
import org.example.interfaces.impl.KeyExpansionImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DesPaddingTest {

    Context context = new Context(TypeAlgorithm.DES, new byte[8], CipherMode.ECB, PaddingMode.ISO_10126, new byte[8], 51, new byte[16]);

    @Test
    void testEmptyInput() {
        byte[] original = new byte[0];
        byte[] padded = context.addPkcs7Padding(original);
        assertEquals(8, padded.length);
        assertArrayEquals(new byte[]{0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08}, padded);

        byte[] unpadded = context.removePkcs7Padding(padded);
        assertArrayEquals(original, unpadded);
    }

    @Test
    void testPaddingAddAndRemove() {

        byte[] data1 = {0x01, 0x02, 0x03};
        byte[] padded1 = context.addPkcs7Padding(data1);
        assertEquals(8, padded1.length);
        assertArrayEquals(new byte[]{0x01, 0x02, 0x03, 0x05, 0x05, 0x05, 0x05, 0x05}, padded1);

        byte[] unpadded1 = context.removePkcs7Padding(padded1);
        assertArrayEquals(data1, unpadded1);


        byte[] data2 = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
        byte[] padded2 = context.addPkcs7Padding(data2);
        assertEquals(16, padded2.length);
        assertArrayEquals(
                new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08},
                padded2
        );

        byte[] unpadded2 = context.removePkcs7Padding(padded2);
        assertArrayEquals(data2, unpadded2);
    }



    @Test
    void testEncryptDecryptShortText() {
        byte[] key = {
                (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0xC4, (byte) 0xC8, (byte) 0xC0,
                (byte) 0xCD, (byte) 0xC0
        };

        byte[] original = "Hello".getBytes();
        byte[] padded = context.addPkcs7Padding(original);

        Des des = new Des(key);
        byte[] encrypted = new byte[padded.length];
        for (int i = 0; i < padded.length; i += 8) {
            byte[] block = new byte[8];
            System.arraycopy(padded, i, block, 0, 8);
            byte[] encryptedBlock = des.encrypt(block);
            System.arraycopy(encryptedBlock, 0, encrypted, i, 8);
        }


        byte[] decryptedPadded = new byte[encrypted.length];
        for (int i = 0; i < encrypted.length; i += 8) {
            byte[] block = new byte[8];
            System.arraycopy(encrypted, i, block, 0, 8);
            byte[] decryptedBlock = des.decrypt(block);
            System.arraycopy(decryptedBlock, 0, decryptedPadded, i, 8);
        }

        byte[] decrypted = context.removePkcs7Padding(decryptedPadded);
        assertArrayEquals(original, decrypted);
    }

    @Test
    void testEncryptDecryptExactBlock() {
        byte[] key = {
                (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0xC4, (byte) 0xC8, (byte) 0xC0,
                (byte) 0xCD, (byte) 0xC0
        };


        byte[] original = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};


        byte[] padded = context.addPkcs7Padding(original);
        assertEquals(16, padded.length);

        Des des = new Des(key);


        byte[] encrypted = new byte[padded.length];
        for (int i = 0; i < padded.length; i += 8) {
            byte[] block = new byte[8];
            System.arraycopy(padded, i, block, 0, 8);
            byte[] encryptedBlock = des.encrypt(block);
            System.arraycopy(encryptedBlock, 0, encrypted, i, 8);
        }


        byte[] decryptedPadded = new byte[encrypted.length];
        for (int i = 0; i < encrypted.length; i += 8) {
            byte[] block = new byte[8];
            System.arraycopy(encrypted, i, block, 0, 8);
            byte[] decryptedBlock = des.decrypt(block);
            System.arraycopy(decryptedBlock, 0, decryptedPadded, i, 8);
        }


        byte[] decrypted = context.removePkcs7Padding(decryptedPadded);
        assertArrayEquals(original, decrypted);
    }


    @Test
    void testFileEncryptionDecryption() throws IOException {

        byte[] key = {
                (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0xC4, (byte) 0xC8, (byte) 0xC0,
                (byte) 0xCD, (byte) 0xC0
        };

        String testContent = "Hello DES! This is a test file content for encryption.";
        Path inputFile = Files.createTempFile("des-test-input", ".txt");
        Files.write(inputFile, testContent.getBytes());


        byte[] fileData = Files.readAllBytes(inputFile);
        byte[] paddedData = context.addPkcs7Padding(fileData);

        Des des = new Des(key);
        byte[] encryptedData = new byte[paddedData.length];
        for (int i = 0; i < paddedData.length; i += 8) {
            byte[] block = new byte[8];
            System.arraycopy(paddedData, i, block, 0, 8);
            byte[] encryptedBlock = des.encrypt(block);
            System.arraycopy(encryptedBlock, 0, encryptedData, i, 8);
        }

        byte[] decryptedPadded = new byte[encryptedData.length];
        for (int i = 0; i < encryptedData.length; i += 8) {
            byte[] block = new byte[8];
            System.arraycopy(encryptedData, i, block, 0, 8);
            byte[] decryptedBlock = des.decrypt(block);
            System.arraycopy(decryptedBlock, 0, decryptedPadded, i, 8);
        }

        byte[] decryptedData = context.removePkcs7Padding(decryptedPadded);
        String decryptedContent = new String(decryptedData);

        assertNotEquals(testContent, new String(encryptedData), "Шифрование не изменило данные");
        assertEquals(testContent, decryptedContent, "Дешифрованные данные не совпадают с исходными");
        assertEquals(fileData.length, decryptedData.length, "Размер данных после дешифрования неверный");

        Files.deleteIfExists(inputFile);
    }

    @Test
    void testEmptyFile() throws IOException {

        byte[] key = {
                (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0xC4, (byte) 0xC8, (byte) 0xC0,
                (byte) 0xCD, (byte) 0xC0
        };

        Path emptyFile = Files.createTempFile("des-test-empty", ".txt");

        byte[] fileData = Files.readAllBytes(emptyFile);
        byte[] paddedData = context.addPkcs7Padding(fileData);
        assertEquals(8, paddedData.length, "Пустой файл должен быть дополнен до 8 байт");


        Des des = new Des(key);
        byte[] encrypted = des.encrypt(paddedData);
        byte[] decryptedPadded = des.decrypt(encrypted);
        byte[] decrypted = context.removePkcs7Padding(decryptedPadded);

        assertEquals(0, decrypted.length, "Пустой файл должен остаться пустым после дешифрования");

        Files.deleteIfExists(emptyFile);
    }


    @Test
    void testZerosPadding_PartialBlock() {
        byte[] data = {0x01, 0x02, 0x03};
        byte[] padded = context.addZerosPadding(data);
        assertEquals(8, padded.length);
        assertArrayEquals(new byte[]{0x01, 0x02, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00}, padded);

        byte[] unpadded = context.removeZerosPadding(padded);
        assertArrayEquals(data, unpadded);
    }

    @Test
    void testZerosPadding_AmbiguousData() {
        byte[] trickyData1 = {0x01, 0x02, 0x00, 0x00};
        byte[] trickyData2 = {0x01, 0x02};

        byte[] padded1 = context.addZerosPadding(trickyData1);
        byte[] padded2 = context.addZerosPadding(trickyData2);
        assertArrayEquals(
                new byte[]{0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
                padded1
        );
        assertArrayEquals(padded1, padded2);

        byte[] unpadded = context.removeZerosPadding(padded1);
        assertArrayEquals(new byte[]{0x01, 0x02}, unpadded);
    }

    @Test
    void testZerosPadding_RealZerosInData() {

        byte[] dataWithMiddleZero = {0x01, 0x00, 0x02, 0x03};
        byte[] padded = context.addZerosPadding(dataWithMiddleZero);

        byte[] unpadded = context.removeZerosPadding(padded);
        assertArrayEquals(dataWithMiddleZero, unpadded);
    }


    @Test
    void testZerosPadding_DataEndsWithZero() {
        byte[] data = {0x01, 0x02, 0x00};
        byte[] padded = context.addZerosPadding(data);


        byte[] unpadded = context.removeZerosPadding(padded);

        assertNotEquals(data.length, unpadded.length);
        assertArrayEquals(new byte[]{0x01, 0x02}, unpadded);
    }

    @Test
    void testZerosPadding_LimitationDocumentation() {
        byte[] originalData1 = {0x01, 0x02, 0x00};
        byte[] originalData2 = {0x01, 0x02};

        byte[] padded1 = context.addZerosPadding(originalData1);
        byte[] padded2 = context.addZerosPadding(originalData2);


        byte[] unpadded1 = context.removeZerosPadding(padded1);
        byte[] unpadded2 = context.removeZerosPadding(padded2);

        assertArrayEquals(originalData2, unpadded2);
        assertArrayEquals(originalData2, unpadded1);
    }


    @Test
    void testAddPadding_EmptyData() {
        byte[] data = new byte[0];
        byte[] padded = context.addIso10126Padding(data);

        assertEquals(8, padded.length);
        assertEquals(padded[padded.length - 1], (byte) 8);
    }

    @Test
    void testAddPadding_PartialBlock() {
        byte[] data = {0x01, 0x02, 0x03};
        byte[] padded = context.addIso10126Padding(data);

        assertEquals(8, padded.length);
        assertEquals(padded[padded.length - 1], (byte) 5);
        assertNotEquals(0, padded[3]);
    }

    @Test
    void testAddPadding_FullBlock() {
        byte[] fullBlock = new byte[8];
        Arrays.fill(fullBlock, (byte) 1);

        byte[] padded = context.addIso10126Padding(fullBlock);

        assertEquals(16, padded.length);
        assertEquals(padded[padded.length - 1], (byte) 8);
    }

    @Test
    void testRemovePadding_NormalCase() {
        byte[] padded = new byte[8];
        Arrays.fill(padded, (byte) 0xAA);
        padded[7] = 3;

        byte[] unpadded = context.removeIso10126Padding(padded);

        assertEquals(5, unpadded.length);
    }

    @Test
    void testRemovePadding_NoPadding() {
        byte[] data = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x00};
        byte[] result = context.removeIso10126Padding(data);

        assertArrayEquals(data, result);
    }

    @Test
    void testRemovePadding_InvalidPadding() {
        byte[] invalidPadding = new byte[8];
        invalidPadding[7] = 9;

        assertThrows(IllegalArgumentException.class, () -> {
            context.removeIso10126Padding(invalidPadding);
        });
    }

    @Test
    void testFullCycle_RandomData() {
        for (int i = 0; i < 20; i++) {
            byte[] original = new byte[i];
            new SecureRandom().nextBytes(original);

            byte[] padded = context.addIso10126Padding(original);
            byte[] unpadded = context.removeIso10126Padding(padded);

            assertArrayEquals(original, unpadded,
                    "Failed for length " + i + ": " + Arrays.toString(original));
        }
    }




    @Test
    void addAnsiX923Padding_ShouldAddFullBlockPadding_WhenInputEmpty() {
        byte[] data = new byte[0];
        byte[] padded = context.addAnsiX923Padding(data);

        assertArrayEquals(
                new byte[]{0, 0, 0, 0, 0, 0, 0, 8},
                padded
        );
    }

    @Test
    void addAnsiX923Padding_ShouldAddPartialPadding_WhenInputNotFullBlock() {
        byte[] data = {0x01, 0x02};
        byte[] padded = context.addAnsiX923Padding(data);

        assertArrayEquals(
                new byte[]{0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 6},
                padded
        );
    }

    @Test
    void addAnsiX923Padding_ShouldAddNewBlock_WhenInputIsFullBlock() {
        byte[] fullBlock = new byte[8];
        Arrays.fill(fullBlock, (byte) 0xFF);

        byte[] padded = context.addAnsiX923Padding(fullBlock);

        assertEquals(16, padded.length);
        assertEquals(8, padded[15]);
    }


    @Test
    void removeAnsiX923Padding_ShouldRemovePadding_WhenPaddingPresent() {
        byte[] padded = {0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06};
        byte[] unpadded = context.removeAnsiX923Padding(padded);

        assertArrayEquals(new byte[]{0x01, 0x02}, unpadded);
    }

    @Test
    void removeAnsiX923Padding_ShouldReturnOriginal_WhenNoPadding() {
        byte[] data = {0x01, 0x02, 0x03, 0x00};
        byte[] result = context.removeAnsiX923Padding(data);

        assertArrayEquals(data, result);
    }

    @Test
    void removeAnsiX923Padding_ShouldThrow_WhenInvalidPaddingLength() {
        byte[] invalid = {0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x09}; // >8

        assertThrows(IllegalArgumentException.class, () -> {
            context.removeAnsiX923Padding(invalid);
        });
    }


    @Test
    void addAndRemoveAnsiX923Padding_ShouldReturnOriginal_ForRandomData() {
        SecureRandom random = new SecureRandom();
        for (int size : new int[]{0, 1, 7, 8, 9, 15, 16}) {
            byte[] original = new byte[size];
            random.nextBytes(original);

            byte[] padded = context.addAnsiX923Padding(original);
            byte[] unpadded = context.removeAnsiX923Padding(padded);

            assertArrayEquals(original, unpadded,
                    "Failed for size: " + size);
        }
    }



}
