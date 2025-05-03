import org.example.rsa.Rsa;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RsaFileTest {
    private static final String TEST_FILE_PATH = "src/test/resources/test.txt";
    private static final int[] KEY_SIZES = {128, 256, 512, 1024};

    @ParameterizedTest
    @EnumSource(Rsa.TestType.class)
    void testFileEncryptionDecryption(Rsa.TestType testType) throws IOException {

        Path filePath = Paths.get(TEST_FILE_PATH);
        byte[] originalBytes = Files.readAllBytes(filePath);
        String originalString = new String(originalBytes);

        BigInteger originalMessage = new BigInteger(1, originalBytes);

        for (int keySize : KEY_SIZES) {

            Rsa rsa = new Rsa(testType, keySize, 0.999);

            BigInteger encrypted = rsa.encrypt(originalMessage);

            BigInteger decrypted = rsa.decrypt(encrypted);

            String decryptedString = new String(decrypted.toByteArray());

            assertEquals(originalMessage, decrypted,
                    String.format("Шифрование/дешифрование должно сохранять исходный текст " +
                            "(тип теста: %s, размер ключа: %d)", testType, keySize));

            assertEquals(originalString, decryptedString);

            System.out.printf("Тест пройден: %s, %d бит%n", testType, keySize);
        }
    }
}
