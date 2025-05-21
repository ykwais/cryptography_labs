package magentaTests;

import lombok.extern.slf4j.Slf4j;
import org.example.constants.CipherMode;
import org.example.constants.PaddingMode;
import org.example.context.Context;
import org.example.magenta.Magenta;
import org.example.magenta.enums.MagentaKeyLength;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
class MagentaWeakTests {
    private static final byte[] TEST_KEY = new byte[16];
    private static final byte[] TEST_IV = new byte[16];
    private static final Integer DELTA = 53;
    private final SecureRandom random = new SecureRandom();

    @TempDir
    Path tempDir;

    private void testModeCombination(CipherMode cipherMode, PaddingMode paddingMode, int dataLength)
            throws IOException {
        byte[] originalData = new byte[dataLength];

        random.nextBytes(originalData);
        originalData[dataLength-1] = (byte) 0x01;

//        for (int i = 0; i < dataLength; i++) {
//            originalData[i] = (byte) i;
//        }

        Context context = new Context(
                new Magenta(MagentaKeyLength.KEY_128, TEST_KEY),
                cipherMode,
                paddingMode,
                TEST_IV,
                DELTA
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
    void shouldHandleExactBlockSize() throws IOException {
        log.info("1");
        testModeCombination(CipherMode.OFB, PaddingMode.PKCS7, 1);
        log.info("2");
        testModeCombination(CipherMode.PCBC, PaddingMode.PKCS7, 16);
        log.info("3");
        testModeCombination(CipherMode.CBC, PaddingMode.PKCS7, 24);
    }


    @Test
    void allModesAndPaddingCombinations_ShouldHandleVariousLengths() {
        int[] lengths = {1, 7, 8, 9, 15, 16, 25, 511, 512, 1023, 4097, 8100, 80000};

        for (CipherMode cipherMode : CipherMode.values()) {
            for (PaddingMode paddingMode : PaddingMode.values()) { // крч может не совпадать Zeros так как удалит лишние нули, но тест с длиной 1 проходит так как затираем не больше 7 байтов
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
}
