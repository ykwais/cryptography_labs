package magentaTests;

import org.example.constants.CipherMode;
import org.example.constants.PaddingMode;
import org.example.context.Context;

import org.example.magenta.Magenta;
import org.example.magenta.enums.MagentaKeyLength;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MagentaFileTests {
    private static final byte[] KEY_128 = new byte[16];
    private static final byte[] KEY_192 = new byte[24];
    private static final byte[] KEY_256 = new byte[32];

    private static final byte[] IV = new byte[16];

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
        MagentaKeyLength[] keyLengths = MagentaKeyLength.values();
        CipherMode[] cipherModes = CipherMode.values();
        PaddingMode[] paddingModes = PaddingMode.values();

        for (MagentaKeyLength keyLength : keyLengths) {

            byte[] key = switch (keyLength) {
                case KEY_128 -> KEY_128;
                case KEY_192 -> KEY_192;
                case KEY_256 -> KEY_256;
            };

            Magenta rc6 = new Magenta(keyLength, key);

            for (CipherMode mode : cipherModes) {
                    if (!mode.equals(CipherMode.RD)) {
                        continue;
                    }
                for (PaddingMode padding : paddingModes) {
                    Context context = new Context(
                            rc6,
                            mode,
                            padding,
                            IV,
                            DELTA
                    );

                    try {
                        System.out.printf("Testing key=%s,  mode=%s, padding=%s, file=%s%n",
                                keyLength,  mode, padding, file.getFileName());
                        testRijndaelEncryptionDecryption(context, file);
                    } catch (AssertionError | IOException e) {
                        fail(String.format("Ошибка: key=%s mode=%s padding=%s file=%s: %s",
                                keyLength, mode, padding, file.getFileName(), e.getMessage()));
                    }
                }
            }

        }
    }
}
