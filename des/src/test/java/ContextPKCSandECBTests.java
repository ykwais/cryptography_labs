import org.example.constants.CipherMode;
import org.example.constants.PaddingMode;
import org.example.constants.TypeAlgorithm;
import org.example.context.Context;
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
                TypeAlgorithm.DES,
                TEST_KEY,
                CipherMode.ECB,
                PaddingMode.PKCS7
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
                TypeAlgorithm.DES,
                TEST_KEY,
                CipherMode.ECB,
                PaddingMode.PKCS7
        );

        context.encrypt(emptyFile, tempEncryptedFile);

        context.decrypt(tempEncryptedFile, tempDecryptedFile);

        assertEquals(0, Files.size(tempDecryptedFile));

        Files.delete(emptyFile);
    }

    @Test
    void testPkcs7Padding() {

        byte[] data = "short".getBytes();
        byte[] padded = Context.addPkcs7Padding(data);
        assertEquals(8, padded.length);
        assertEquals(3, padded[padded.length - 1]);

        byte[] unpadded = Context.removePkcs7Padding(padded);
        assertArrayEquals(data, unpadded);
    }

    @Test
    void testFullBlockPadding() {

        byte[] fullBlock = new byte[8];
        new Random().nextBytes(fullBlock);

        byte[] padded = Context.addPkcs7Padding(fullBlock);
        assertEquals(16, padded.length);

        byte[] unpadded = Context.removePkcs7Padding(padded);
        assertArrayEquals(fullBlock, unpadded);
    }

    @Test
    void testParallelProcessing() {
        Context context = new Context(
                TypeAlgorithm.DES,
                TEST_KEY,
                CipherMode.ECB,
                PaddingMode.PKCS7
        );

        byte[] largeData = new byte[4096]; // 4KB данных
        new Random().nextBytes(largeData);

        byte[] encrypted = context.encryptDecryptInner(largeData, true);
        assertNotNull(encrypted);
        assertEquals(largeData.length, encrypted.length);

        byte[] decrypted = context.encryptDecryptInner(encrypted, false);
        assertArrayEquals(largeData, decrypted);
    }
}
