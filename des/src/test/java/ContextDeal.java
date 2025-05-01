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

class ContextDeal {
    private static final byte[] TEST_KEY = new byte[8];
    private static final byte[] TEST_IV = new byte[16];
    private static final byte[] TEST_KEY_DEAL_128 = new byte[16];
    private static final byte[] TEST_KEY_DEAL_192 = new byte[24];
    private static final byte[] TEST_KEY_DEAL_256 = new byte[32];
    private static final int TEST_DELTA = 53;
    private final SecureRandom random = new SecureRandom();

    @TempDir
    Path tempDir;

    @Test
    void allModesAndPaddingCombinations_ShouldHandleVariousLengths_1() {
        int[] lengths = {1, 7, 8, 9, 15, 16, 511, 512, 1023, 4097, 8100, 80000};

        for (CipherMode cipherMode : CipherMode.values()) {
            for (PaddingMode paddingMode : PaddingMode.values()) { // крч может не совпадать Zeros так как удалит лишние нули, но тест с длиной 1 проходит так как затираем не больше 7 байтов
                System.out.println("Testing cipher mode: " + cipherMode + " with padding: " + paddingMode);

                for (int len : lengths) {
                    try {
                        System.out.println("  Length: " + len);
                        testModeCombination_1(cipherMode, paddingMode, len);
                    } catch (Exception e) {
                        fail("Failed for " + cipherMode + "/" + paddingMode +
                                " with length " + len + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    @Test
    void allModesAndPaddingCombinations_ShouldHandleVariousLengths_2() {
        int[] lengths = {1, 7, 8, 9, 15, 16, 511, 512, 1023, 4097, 8100, 80000};

        for (CipherMode cipherMode : CipherMode.values()) {
            for (PaddingMode paddingMode : PaddingMode.values()) { // крч может не совпадать Zeros так как удалит лишние нули, но тест с длиной 1 проходит так как затираем не больше 7 байтов
                System.out.println("Testing cipher mode: " + cipherMode + " with padding: " + paddingMode);

                for (int len : lengths) {
                    try {
                        System.out.println("  Length: " + len);
                        testModeCombination_2(cipherMode, paddingMode, len);
                    } catch (Exception e) {
                        fail("Failed for " + cipherMode + "/" + paddingMode +
                                " with length " + len + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    @Test
    void allModesAndPaddingCombinations_ShouldHandleVariousLengths_3() {
        int[] lengths = {1, 7, 8, 9, 15, 16, 511, 512, 1023, 4097, 8100, 80000};

        for (CipherMode cipherMode : CipherMode.values()) {
            for (PaddingMode paddingMode : PaddingMode.values()) { // крч может не совпадать Zeros так как удалит лишние нули, но тест с длиной 1 проходит так как затираем не больше 7 байтов
                System.out.println("Testing cipher mode: " + cipherMode + " with padding: " + paddingMode);

                for (int len : lengths) {
                    try {
                        System.out.println("  Length: " + len);
                        testModeCombination_3(cipherMode, paddingMode, len);
                    } catch (Exception e) {
                        fail("Failed for " + cipherMode + "/" + paddingMode +
                                " with length " + len + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    private void testModeCombination_1(CipherMode cipherMode, PaddingMode paddingMode, int dataLength)
            throws IOException {
        byte[] originalData = new byte[dataLength];
        random.nextBytes(originalData);
        originalData[dataLength-1] = (byte) 0x01;

        Context context = new Context(
                TypeAlgorithm.DEAL_128,
                TEST_KEY,
                cipherMode,
                paddingMode,
                TEST_IV,
                TEST_KEY_DEAL_128,
                TEST_DELTA
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

    private void testModeCombination_2(CipherMode cipherMode, PaddingMode paddingMode, int dataLength)
            throws IOException {
        byte[] originalData = new byte[dataLength];
        random.nextBytes(originalData);
        originalData[dataLength-1] = (byte) 0x01;


        Context context = new Context(
                TypeAlgorithm.DEAL_192,
                TEST_KEY,
                cipherMode,
                paddingMode,
                TEST_IV,
                TEST_KEY_DEAL_192,
                TEST_DELTA
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

    private void testModeCombination_3(CipherMode cipherMode, PaddingMode paddingMode, int dataLength)
            throws IOException {
        byte[] originalData = new byte[dataLength];
        random.nextBytes(originalData);
        originalData[dataLength-1] = (byte) 0x01;



        Context context = new Context(
                TypeAlgorithm.DEAL_256,
                TEST_KEY,
                cipherMode,
                paddingMode,
                TEST_IV,
                TEST_KEY_DEAL_256,
                TEST_DELTA
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
}
