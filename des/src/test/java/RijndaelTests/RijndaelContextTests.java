package RijndaelTests;

import org.example.constants.CipherMode;
import org.example.constants.PaddingMode;
import org.example.context.Context;
import org.example.des.Des;
import org.example.rijnadael.Rijndael;
import org.example.rijnadael.enums.RijndaelBlockLength;
import org.example.rijnadael.enums.RijndaelKeyLength;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class RijndaelContextTests {

    private static final byte[] TEST_KEY = new byte[16];
    private static final byte[] TEST_IV = new byte[16];
    private static final Integer DELTA = 53;
    private final SecureRandom random = new SecureRandom();

    @TempDir
    Path tempDir;

    private void testModeCombination(CipherMode cipherMode, PaddingMode paddingMode, int dataLength)
            throws IOException {
        byte[] originalData = new byte[dataLength];

        //random.nextBytes(originalData);
        //originalData[dataLength-1] = (byte) 0x01;

        for (int i = 0; i < dataLength; i++) {
            originalData[i] = (byte) i;
        }

        Context context = new Context(
                new Rijndael(RijndaelKeyLength.KEY_128, RijndaelBlockLength.BLOCK_128, TEST_KEY),
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
        //testModeCombination(CipherMode.CBC, PaddingMode.PKCS7, 8);
        testModeCombination(CipherMode.PCBC, PaddingMode.PKCS7, 16);
        //testModeCombination(CipherMode.CBC, PaddingMode.PKCS7, 24);
    }
}
