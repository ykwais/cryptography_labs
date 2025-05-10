import org.example.constants.BitsInKeysOfDeal;
import org.example.constants.CipherMode;
import org.example.constants.PaddingMode;
import org.example.context.Context;
import org.example.deal.Deal;
import org.example.des.Des;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class FileEncrypt {
    private static final byte[] TEST_KEY_DES = new byte[8];
    private static final byte[] TEST_KEY_DEAL_128 = new byte[16];
    private static final byte[] TEST_KEY_DEAL_192 = new byte[24];
    private static final byte[] TEST_KEY_DEAL_256 = new byte[32];
    private static final byte[] TEST_IV_DEAL = new byte[16];
    private static final byte[] TEST_IV_DES = new byte[8];

    private static final int TEST_DELTA = 53;

    @TempDir
    Path tempDir;

    static {
        new SecureRandom().nextBytes(TEST_KEY_DES);
        new SecureRandom().nextBytes(TEST_KEY_DEAL_128);
        new SecureRandom().nextBytes(TEST_KEY_DEAL_192);
        new SecureRandom().nextBytes(TEST_KEY_DEAL_256);
        new SecureRandom().nextBytes(TEST_IV_DEAL);
        new SecureRandom().nextBytes(TEST_IV_DES);
    }


    @ParameterizedTest
    @MethodSource("provideTestFiles")
    void testDesEncryptionDecryption(Path originalFile) throws IOException {
        assumeTrue(Files.exists(originalFile), "Test file not found: " + originalFile);


        Context context = new Context(
                new Des(TEST_KEY_DES),
                CipherMode.RD,
                PaddingMode.PKCS7,
                TEST_IV_DES,
                TEST_DELTA
        );

        testFileEncryptionDecryption(context, originalFile);
    }

    @ParameterizedTest
    @MethodSource("provideTestFiles")
    void testDeal128EncryptionDecryption(Path originalFile) throws IOException {
        assumeTrue(Files.exists(originalFile), "Test file not found: " + originalFile);

        Context context = new Context(
                new Deal(BitsInKeysOfDeal.BIT_128, TEST_KEY_DES, TEST_KEY_DEAL_128),
                CipherMode.CTR,
                PaddingMode.PKCS7,
                TEST_IV_DEAL,
                TEST_DELTA
        );

        testFileEncryptionDecryption(context, originalFile);
    }

    private void testFileEncryptionDecryption(Context context, Path originalFile) throws IOException {
        String fileName = originalFile.getFileName().toString();
        Path encryptedFile = tempDir.resolve("encrypted_" + fileName);
        Path decryptedFile = tempDir.resolve("decrypted_" + fileName);


        context.encrypt(originalFile, encryptedFile);


        assertTrue(Files.exists(encryptedFile), "Encrypted file not created");
        assertNotEquals(Files.size(originalFile), Files.size(encryptedFile),
                "File sizes should differ after encryption");


        context.decrypt(encryptedFile, decryptedFile);


        assertTrue(Files.exists(decryptedFile), "Decrypted file not created");

        assertTrue(Files.mismatch(originalFile, decryptedFile) == -1,
                "Original and decrypted files differ");


        if (fileName.endsWith(".txt")) {
            String originalContent = Files.readString(originalFile);
            String decryptedContent = Files.readString(decryptedFile);
            assertEquals(originalContent, decryptedContent,
                    "Text content differs after decryption");
        }
    }

    static Stream<Path> provideTestFiles() {
        return Stream.of(
                Paths.get("src/main/resources/second.txt"),
                Paths.get("src/main/resources/пз_3_менеджмент.pdf"),
                Paths.get("src/main/resources/Архиватор на Go _ Урок #7_ Алгоритм Шеннона-Фано - теория.mp4"),
                Paths.get("src/main/resources/in.png")
        ).filter(Files::exists);
    }

    @ParameterizedTest
    @EnumSource(CipherMode.class)
    void testDesWithDifferentModes(CipherMode mode) throws IOException {
        Path testFile = Paths.get("src/main/resources/second.txt");
        assumeTrue(Files.exists(testFile));

        Context context = new Context(
                new Des(TEST_KEY_DES),
                mode,
                PaddingMode.PKCS7,
                TEST_IV_DES,
                TEST_DELTA
        );

        testFileEncryptionDecryption(context, testFile);
    }

    @ParameterizedTest
    @EnumSource(PaddingMode.class)
    void testDeal256WithDifferentPadding(PaddingMode padding) throws IOException {
        Path testFile = Paths.get("src/main/resources/пз_3_менеджмент.pdf");
        assumeTrue(Files.exists(testFile));

        Context context = new Context(
                new Deal(BitsInKeysOfDeal.BIT_256, TEST_KEY_DES, TEST_KEY_DEAL_256),
                CipherMode.CBC,
                padding,
                TEST_IV_DEAL,
                TEST_DELTA
        );

        testFileEncryptionDecryption(context, testFile);
    }

    @ParameterizedTest
    @EnumSource(CipherMode.class)
    void testDealWithDifferentModes(CipherMode mode) throws IOException {
        Path testFile = Paths.get("src/main/resources/second.txt");
        assumeTrue(Files.exists(testFile));

        Context context = new Context(
                new Deal(BitsInKeysOfDeal.BIT_256, TEST_KEY_DES, TEST_KEY_DEAL_256),
                mode,
                PaddingMode.PKCS7,
                TEST_IV_DEAL,
                TEST_DELTA
        );

        testFileEncryptionDecryption(context, testFile);
    }

    @ParameterizedTest
    @EnumSource(PaddingMode.class)
    void testDesWithDifferentPadding(PaddingMode padding) throws IOException {
        Path testFile = Paths.get("src/main/resources/пз_3_менеджмент.pdf");
        assumeTrue(Files.exists(testFile));

        Context context = new Context(
                new Des(TEST_KEY_DES),
                CipherMode.RD,
                padding,
                TEST_IV_DES,
                TEST_DELTA
        );

        testFileEncryptionDecryption(context, testFile);
    }

}
