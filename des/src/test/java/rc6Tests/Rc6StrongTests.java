package rc6Tests;

import org.example.constants.CipherMode;
import org.example.constants.PaddingMode;
import org.example.context.Context;
import org.example.interfaces.EncryptorDecryptorSymmetric;
import org.example.rc6.RC6;
import org.example.rc6.enums.RC6KeyLength;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

class Rc6StrongTests {
    private static final Integer DELTA = 53;
    private final SecureRandom random = new SecureRandom();

    @TempDir
    Path tempDir;

    private void testModeCombination(RC6KeyLength keyLength,
                                     CipherMode cipherMode,
                                     PaddingMode paddingMode,
                                     int dataLength) throws IOException {

        int keyBytes   = keyLength.getKeyLengthInBytes();


        byte[] key = new byte[keyBytes];
        random.nextBytes(key);

        EncryptorDecryptorSymmetric cipher = new RC6(keyLength, key);
        int blockBytes = cipher.getBlockSize();

        byte[] iv  = new byte[blockBytes];
        random.nextBytes(iv);


        byte[] originalData = new byte[dataLength];
        random.nextBytes(originalData);

        originalData[dataLength - 1] = 0x01;

        Context context = new Context(
                cipher,
                cipherMode,
                paddingMode,
                iv,
                DELTA
        );

        Path inputFile     = tempDir.resolve(
                String.format("in_%s_%s_%s_%d.bin",
                        keyLength, cipherMode, paddingMode, dataLength));
        Path encryptedFile = tempDir.resolve(
                String.format("enc_%s_%s_%s_%d.bin",
                        keyLength, cipherMode, paddingMode, dataLength));
        Path decryptedFile = tempDir.resolve(
                String.format("dec_%s_%s_%s_%d.bin",
                        keyLength, cipherMode, paddingMode, dataLength));

        Files.write(inputFile, originalData);
        context.encrypt(inputFile, encryptedFile);
        context.decrypt(encryptedFile, decryptedFile);

        byte[] decryptedData = Files.readAllBytes(decryptedFile);
        assertArrayEquals(originalData, decryptedData,
                String.format("Ошибка: key=%s mode=%s padding=%s length=%d",
                        keyLength, cipherMode, paddingMode, dataLength));
    }

    @Test
    void allKeyBlockModePaddingCombinations() {

        int[] lengths = {1, 7, 8, 9, 15, 16, 31, 32, 63, 64, 511, 512, 1023, 4097, 8100, 80000};


            for (RC6KeyLength blkLen : RC6KeyLength.values()) {
                for (CipherMode cm : CipherMode.values()) {
                    for (PaddingMode pm : PaddingMode.values()) {
                        for (int len : lengths) {
                            try {
                                testModeCombination(blkLen, cm, pm, len);
                            } catch (Exception ex) {
                                fail(String.format(
                                        "Не удалось для key=%s mode=%s padding=%s len=%d → %s",
                                        blkLen, cm, pm, len, ex.getMessage()
                                ));
                            }
                        }
                    }
                }
            }

    }
}
