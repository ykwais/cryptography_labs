import lombok.extern.slf4j.Slf4j;
import org.example.constants.CipherMode;
import org.example.constants.PaddingMode;
import org.example.constants.TypeAlgorithm;
import org.example.context.Context;
import org.example.des.Des;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Arrays;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;


@Slf4j
class ContextAllPaddingTest {
    private static final byte[] TEST_KEY = new byte[8];
    private static final byte[] TEST_IV = new byte[8];
    private final SecureRandom random = new SecureRandom();

    @TempDir
    Path tempDir;


    private void testPaddingMode(PaddingMode mode, int dataLength) throws IOException {

        byte[] originalData = new byte[dataLength];
        random.nextBytes(originalData);

        Context context = new Context(
                new Des(TEST_KEY),
                CipherMode.ECB,
                mode,
                TEST_IV
        );

        Path inputFile = tempDir.resolve("input.bin");
        Path encryptedFile = tempDir.resolve("encrypted.bin");
        Path decryptedFile = tempDir.resolve("decrypted.bin");

        Files.write(inputFile, originalData);

        context.encrypt(inputFile, encryptedFile);
        context.decrypt(encryptedFile, decryptedFile);

        byte[] decryptedData = Files.readAllBytes(decryptedFile);
        assertArrayEquals(originalData, decryptedData,
                "Failed for " + mode + " with length " + dataLength);
    }


    @Test
    void pkcs7_ShouldHandlePartialBlock() throws IOException {
        testPaddingMode(PaddingMode.PKCS7, 7);
    }

    @Test
    void pkcs7_ShouldHandleEmptyData() throws IOException {
        testPaddingMode(PaddingMode.PKCS7, 0);
    }

    @Test
    void zeros_ShouldHandlePartialBlock() throws IOException {
        testPaddingMode(PaddingMode.ZEROS, 5);
    }

    @Test
    void zeros_ShouldHandleDataEndingWithZero() throws IOException {
        byte[] dataWithZero = new byte[7];
        Arrays.fill(dataWithZero, (byte) 1);
        dataWithZero[6] = 8;

        Context context = new Context(
                new Des(TEST_KEY),
                CipherMode.ECB,
                PaddingMode.ZEROS,
                TEST_IV
        );

        Path inputFile = tempDir.resolve("zeros.bin");
        Files.write(inputFile, dataWithZero);

        context.encrypt(inputFile, tempDir.resolve("enc.bin"));
        context.decrypt(tempDir.resolve("enc.bin"), tempDir.resolve("dec.bin"));

        byte[] decrypted = Files.readAllBytes(tempDir.resolve("dec.bin"));
        assertArrayEquals(dataWithZero, decrypted);
    }

    @Test
    void ansiX923_ShouldAddCorrectPadding() throws IOException {
        testPaddingMode(PaddingMode.ANSI_X923, 3);
    }

    @Test
    void iso10126_ShouldHandleRandomPadding() throws IOException {
        testPaddingMode(PaddingMode.ISO_10126, 6);
    }


    @Test
    void allModes_ShouldHandleVariousLengths() throws IOException {

        int[] lengths = {1, 7, 8, 9, 15, 16, 511, 512, 1023, 4097, 8100, 8000000};

        for (PaddingMode mode : PaddingMode.values()) {
            System.out.println("Testing mode: " + mode);

            for (int len : lengths) {
                try {
                    System.out.println("  Length: " + len);
                    testPaddingMode(mode, len);
                } catch (Exception e) {
                    fail("Failed for " + mode + " with length " + len + ": " + e.getMessage());
                }
            }
        }
    }
}
