import org.example.des.Des;
import org.example.interfaces.impl.FiestelFunction;
import org.example.interfaces.impl.KeyExpansionImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DesPaddingPKCSTest {

    @Test
    void testEmptyInput() {
        byte[] original = new byte[0];
        byte[] padded = Des.addPkcs7Padding(original);
        assertEquals(8, padded.length);
        assertArrayEquals(new byte[]{0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08}, padded);

        byte[] unpadded = Des.removePkcs7Padding(padded);
        assertArrayEquals(original, unpadded);
    }

    @Test
    void testPaddingAddAndRemove() {

        byte[] data1 = {0x01, 0x02, 0x03};
        byte[] padded1 = Des.addPkcs7Padding(data1);
        assertEquals(8, padded1.length);
        assertArrayEquals(new byte[]{0x01, 0x02, 0x03, 0x05, 0x05, 0x05, 0x05, 0x05}, padded1);

        byte[] unpadded1 = Des.removePkcs7Padding(padded1);
        assertArrayEquals(data1, unpadded1);


        byte[] data2 = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
        byte[] padded2 = Des.addPkcs7Padding(data2);
        assertEquals(16, padded2.length);
        assertArrayEquals(
                new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08},
                padded2
        );

        byte[] unpadded2 = Des.removePkcs7Padding(padded2);
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
        byte[] padded = Des.addPkcs7Padding(original);

        Des des = new Des(key, new KeyExpansionImpl(), new FiestelFunction());
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

        byte[] decrypted = Des.removePkcs7Padding(decryptedPadded);
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


        byte[] padded = Des.addPkcs7Padding(original);
        assertEquals(16, padded.length);

        Des des = new Des(key, new KeyExpansionImpl(), new FiestelFunction());


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


        byte[] decrypted = Des.removePkcs7Padding(decryptedPadded);
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
        byte[] paddedData = Des.addPkcs7Padding(fileData);

        Des des = new Des(key, new KeyExpansionImpl(), new FiestelFunction());
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

        byte[] decryptedData = Des.removePkcs7Padding(decryptedPadded);
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
        byte[] paddedData = Des.addPkcs7Padding(fileData);
        assertEquals(8, paddedData.length, "Пустой файл должен быть дополнен до 8 байт");


        Des des = new Des(key, new KeyExpansionImpl(), new FiestelFunction());
        byte[] encrypted = des.encrypt(paddedData);
        byte[] decryptedPadded = des.decrypt(encrypted);
        byte[] decrypted = Des.removePkcs7Padding(decryptedPadded);

        assertEquals(0, decrypted.length, "Пустой файл должен остаться пустым после дешифрования");

        Files.deleteIfExists(emptyFile);
    }
}
