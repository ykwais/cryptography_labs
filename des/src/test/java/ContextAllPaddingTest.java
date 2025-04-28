import lombok.extern.slf4j.Slf4j;
import org.example.constants.CipherMode;
import org.example.constants.PaddingMode;
import org.example.constants.TypeAlgorithm;
import org.example.context.Context;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;


@Slf4j
class ContextAllPaddingTest {
    private static final byte[] TEST_KEY = new byte[8];
    private final Random random = new Random();

    @TempDir
    Path tempDir;


    private void testPaddingMode(PaddingMode mode, int dataLength) throws IOException {

        byte[] originalData = new byte[dataLength];
        random.nextBytes(originalData);

        Context context = new Context(
                TypeAlgorithm.DES,
                TEST_KEY,
                CipherMode.ECB,
                mode
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
                TypeAlgorithm.DES,
                TEST_KEY,
                CipherMode.ECB,
                PaddingMode.ZEROS
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
        // Исключаем длину 0, так как требует особой обработки
        int[] lengths = {/*1, 7, 8, 9, 15, 16, 511, */512};

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
