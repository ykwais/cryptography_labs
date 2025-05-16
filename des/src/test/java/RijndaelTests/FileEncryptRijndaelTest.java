package RijndaelTests;

import org.example.constants.CipherMode;
import org.example.constants.PaddingMode;
import org.example.context.Context;
import org.example.rijnadael.Rijndael;
import org.example.rijnadael.enums.RijndaelBlockLength;
import org.example.rijnadael.enums.RijndaelKeyLength;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileEncryptRijndaelTest {
    private static final byte[] KEY_128 = new byte[16];
    private static final byte[] KEY_192 = new byte[24];
    private static final byte[] KEY_256 = new byte[32];

    private static final byte[] IV = new byte[32]; // максимально возможный IV

    private static final int DELTA = 53;

    @TempDir
    Path tempDir;

    static {
        SecureRandom random = new SecureRandom();
        random.nextBytes(KEY_128);
        random.nextBytes(KEY_192);
        random.nextBytes(KEY_256);
        random.nextBytes(IV);
    }

    private byte[] getIVForBlockSize(RijndaelBlockLength blockLength) {
        return switch (blockLength) {
            case BLOCK_128 -> Arrays.copyOf(IV, 16);
            case BLOCK_192 -> Arrays.copyOf(IV, 24);
            case BLOCK_256 -> Arrays.copyOf(IV, 32);
        };
    }

    static Stream<Path> provideTestFiles() {
        return Stream.of(
                Paths.get("src/main/resources/second.txt"),
                Paths.get("src/main/resources/пз_3_менеджмент.pdf"),
                Paths.get("src/main/resources/Архиватор на Go _ Урок #7_ Алгоритм Шеннона-Фано - теория.mp4"),
                Paths.get("src/main/resources/in.png")
        ).filter(Files::exists);
    }

    private void testRijndaelEncryptionDecryption(Context context, Path originalFile) throws IOException {
        String fileName = originalFile.getFileName().toString();
        Path encryptedFile = tempDir.resolve("encrypted_" + fileName);
        Path decryptedFile = tempDir.resolve("decrypted_" + fileName);

        context.encrypt(originalFile, encryptedFile);
        assertTrue(Files.exists(encryptedFile));

        context.decrypt(encryptedFile, decryptedFile);
        assertTrue(Files.exists(decryptedFile));

        assertTrue(Files.mismatch(originalFile, decryptedFile) == -1,
                "Original and decrypted files differ");

        if (fileName.endsWith(".txt")) {
            assertEquals(Files.readString(originalFile), Files.readString(decryptedFile));
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestFiles")
    void testAllRijndaelCombinations(Path file) throws IOException {
        RijndaelKeyLength[] keyLengths = RijndaelKeyLength.values();
        RijndaelBlockLength[] blockLengths = RijndaelBlockLength.values();
        CipherMode[] cipherModes = CipherMode.values();
        PaddingMode[] paddingModes = PaddingMode.values();

        for (RijndaelKeyLength keyLength : keyLengths) {
            for (RijndaelBlockLength blockLength : blockLengths) {
                byte[] key = switch (keyLength) {
                    case KEY_128 -> KEY_128;
                    case KEY_192 -> KEY_192;
                    case KEY_256 -> KEY_256;
                };

                Rijndael rijndael = new Rijndael(keyLength, blockLength, key, (byte) 0x1B);

                for (CipherMode mode : cipherModes) {
                    if (!mode.equals(CipherMode.RD)) {
                        continue;
                    }
                    for (PaddingMode padding : paddingModes) {
                        byte[] iv = getIVForBlockSize(blockLength);
                        Context context = new Context(
                                rijndael,
                                mode,
                                padding,
                                iv,
                                DELTA
                        );

                        try {
                            System.out.printf("Testing key=%s, block=%s, mode=%s, padding=%s, file=%s%n",
                                    keyLength, blockLength, mode, padding, file.getFileName());
                            testRijndaelEncryptionDecryption(context, file);
                        } catch (AssertionError | IOException e) {
                            fail(String.format("Ошибка: key=%s block=%s mode=%s padding=%s file=%s: %s",
                                    keyLength, blockLength, mode, padding, file.getFileName(), e.getMessage()));
                        }
                    }
                }
            }
        }
    }
}
