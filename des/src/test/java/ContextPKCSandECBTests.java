import org.example.constants.CipherMode;
import org.example.constants.PaddingMode;
import org.example.constants.TypeAlgorithm;
import org.example.context.Context;
import org.example.des.Des;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ContextPKCSandECBTests {
    private static final byte[] TEST_KEY = {0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF};
    private static final byte[] TEST_KEY_DEAL = {0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF, 0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF};

    private static final byte[] TEST_IV = {0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF};
    private static final String TEST_TEXT = "Hello DES Encryption! Тест 1234";
    private static Path tempInputFile;
    private static Path tempEncryptedFile;
    private static Path tempDecryptedFile;

    @BeforeAll
    static void setup() throws IOException {
        tempInputFile = Files.createTempFile("des-test-input", ".txt");
        tempEncryptedFile = Files.createTempFile("des-test-encrypted", ".bin");
        tempDecryptedFile = Files.createTempFile("des-test-decrypted", ".txt");

        Files.write(tempInputFile, TEST_TEXT.getBytes());
    }

    @AfterAll
    static void cleanup() throws IOException {
        Files.deleteIfExists(tempInputFile);
        Files.deleteIfExists(tempEncryptedFile);
        Files.deleteIfExists(tempDecryptedFile);
    }

    @Test
    void testEncryptDecryptFile() throws Exception {
        Context context = new Context(
                new Des(TEST_KEY),
                CipherMode.ECB,
                PaddingMode.PKCS7,
                TEST_IV
        );


        context.encrypt(tempInputFile, tempEncryptedFile);


        byte[] encryptedData = Files.readAllBytes(tempEncryptedFile);
        assertNotEquals(TEST_TEXT, new String(encryptedData));
        assertTrue(encryptedData.length >= TEST_TEXT.getBytes().length);


        context.decrypt(tempEncryptedFile, tempDecryptedFile);

        byte[] decryptedData = Files.readAllBytes(tempDecryptedFile);
        assertEquals(TEST_TEXT, new String(decryptedData));
    }

    @Test
    void testEmptyFile() throws Exception {
        Path emptyFile = Files.createTempFile("des-empty", ".txt");
        Context context = new Context(
                new Des(TEST_KEY),
                CipherMode.ECB,
                PaddingMode.PKCS7,
                TEST_IV
        );

        context.encrypt(emptyFile, tempEncryptedFile);

        context.decrypt(tempEncryptedFile, tempDecryptedFile);

        assertEquals(0, Files.size(tempDecryptedFile));

        Files.delete(emptyFile);
    }

    @Test
    void testPkcs7Padding() {
        Context context = new Context(
                new Des(TEST_KEY),
                CipherMode.ECB,
                PaddingMode.PKCS7,
                TEST_IV
        );

        byte[] data = "short".getBytes();
        byte[] padded = context.addPkcs7Padding(data);
        assertEquals(8, padded.length);
        assertEquals(3, padded[padded.length - 1]);

        byte[] unpadded = context.removePkcs7Padding(padded);
        assertArrayEquals(data, unpadded);
    }

    @Test
    void testFullBlockPadding() {

        Context context = new Context(
                new Des(TEST_KEY),
                CipherMode.ECB,
                PaddingMode.PKCS7,
                TEST_IV
        );

        byte[] fullBlock = new byte[8];
        new Random().nextBytes(fullBlock);

        byte[] padded = context.addPkcs7Padding(fullBlock);
        assertEquals(16, padded.length);

        byte[] unpadded = context.removePkcs7Padding(padded);
        assertArrayEquals(fullBlock, unpadded);
    }

//    @Test
//    void testParallelProcessing() {
//        Context context = new Context(
//                new Des(TEST_KEY),
//                CipherMode.ECB,
//                PaddingMode.PKCS7,
//                TEST_IV
//        );
//
//        byte[] largeData = new byte[4096];
//        new Random().nextBytes(largeData);
//
//        byte[] encrypted = context.encryptDecryptInner(largeData, true);
//        assertNotNull(encrypted);
//        assertEquals(largeData.length, encrypted.length);
//
//        byte[] decrypted = context.encryptDecryptInner(encrypted, false);
//        assertArrayEquals(largeData, decrypted);
//    }

    @Test
    void testCBC() throws Exception {
        Context context = new Context(
                new Des(TEST_KEY),
                CipherMode.CBC,
                PaddingMode.PKCS7,
                TEST_IV
        );


        context.encrypt(tempInputFile, tempEncryptedFile);


        byte[] encryptedData = Files.readAllBytes(tempEncryptedFile);
        assertNotEquals(TEST_TEXT, new String(encryptedData));
        assertTrue(encryptedData.length >= TEST_TEXT.getBytes().length);


        context.decrypt(tempEncryptedFile, tempDecryptedFile);

        byte[] decryptedData = Files.readAllBytes(tempDecryptedFile);
        assertEquals(TEST_TEXT, new String(decryptedData));
    }



    @Test
    void testEmptyFileCBC() throws Exception {
        Path emptyFile = Files.createTempFile("des-empty", ".txt");
        Context context = new Context(
                new Des(TEST_KEY),
                CipherMode.CBC,
                PaddingMode.PKCS7,
                TEST_IV
        );

        context.encrypt(emptyFile, tempEncryptedFile);

        context.decrypt(tempEncryptedFile, tempDecryptedFile);

        assertEquals(0, Files.size(tempDecryptedFile));

        Files.delete(emptyFile);
    }
}
