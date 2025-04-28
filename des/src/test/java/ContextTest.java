import org.example.constants.CipherMode;
import org.example.constants.PaddingMode;
import org.example.constants.TypeAlgorithm;
import org.example.context.Context;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ContextTest {
    private static final byte[] TEST_KEY = new byte[8];
    private static final byte[] TEST_IV = new byte[8];
    private final SecureRandom random = new SecureRandom();

    @TempDir
    Path tempDir;

    @Test
    void allModesAndPaddingCombinations_ShouldHandleVariousLengths() {
        int[] lengths = {1, 7, 8, 9, 15, 16, 511, 512, 1023, 4097, 8100, 80000};

        for (CipherMode cipherMode : CipherMode.values()) {
            for (PaddingMode paddingMode : PaddingMode.values()) {
                System.out.println("Testing cipher mode: " + cipherMode + " with padding: " + paddingMode);

                for (int len : lengths) {
                    try {
                        System.out.println("  Length: " + len);
                        testModeCombination(cipherMode, paddingMode, len);
                    } catch (Exception e) {
                        fail("Failed for " + cipherMode + "/" + paddingMode +
                                " with length " + len + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    private void testModeCombination(CipherMode cipherMode, PaddingMode paddingMode, int dataLength)
            throws IOException {
        byte[] originalData = new byte[dataLength];
        random.nextBytes(originalData);

        Context context = new Context(
                TypeAlgorithm.DES,
                TEST_KEY,
                cipherMode,
                paddingMode,
                TEST_IV
        );

        Path inputFile = tempDir.resolve("input_" + cipherMode + "_" + paddingMode + "_" + dataLength + ".bin");
        Path encryptedFile = tempDir.resolve("encrypted_" + cipherMode + "_" + paddingMode + "_" + dataLength + ".bin");
        Path decryptedFile = tempDir.resolve("decrypted_" + cipherMode + "_" + paddingMode + "_" + dataLength + ".bin");

        Files.write(inputFile, originalData);
        context.encrypt(inputFile, encryptedFile);
        context.decrypt(encryptedFile, decryptedFile);

        byte[] decryptedData = Files.readAllBytes(decryptedFile);
        assertArrayEquals(originalData, decryptedData,
                "Failed for " + cipherMode + "/" + paddingMode + " with length " + dataLength);
    }

    @Test
    void shouldHandleEmptyFile() throws IOException {
        testModeCombination(CipherMode.ECB, PaddingMode.PKCS7, 0);
        testModeCombination(CipherMode.CBC, PaddingMode.ISO_10126, 0);
    }

    @Test
    void shouldHandleLargeFile() throws IOException {
        testModeCombination(CipherMode.ECB, PaddingMode.PKCS7, 10_000_000);
        testModeCombination(CipherMode.CBC, PaddingMode.ANSI_X923, 10_000_000);
    }

    @Test
    void shouldHandleExactBlockSize() throws IOException {
        testModeCombination(CipherMode.CBC, PaddingMode.PKCS7, 8);
        testModeCombination(CipherMode.CBC, PaddingMode.PKCS7, 16);
        testModeCombination(CipherMode.CBC, PaddingMode.PKCS7, 24);
    }

    @Test
    void shouldHandleOneByteLessThanBlockSize() throws IOException {
        testModeCombination(CipherMode.CBC, PaddingMode.ISO_10126, 7);
        testModeCombination(CipherMode.CBC, PaddingMode.ISO_10126, 15);
        testModeCombination(CipherMode.CBC, PaddingMode.ISO_10126, 23);
    }
}
