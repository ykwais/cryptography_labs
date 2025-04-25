import org.example.des.Des;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DesEncryptionOneBlockTest {

    private final Des des = new Des();

    private byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i*2, i*2+2), 16);
        }
        return bytes;
    }

    @Test
    void testWithNistKnownAnswer() {
        byte[] key = hexToBytes("133457799BBCDFF1");
        byte[] plaintext = hexToBytes("0123456789ABCDEF");

        byte[] ciphertext = des.encode(plaintext, key);
        byte[] expected = hexToBytes("85E813540F0AB405");

        assertArrayEquals(expected, ciphertext,
                "Шифрование не соответствует NIST тестовому вектору");

        byte[] decrypted = des.decode(ciphertext, key);
        assertArrayEquals(plaintext, decrypted,
                "Дешифровка не вернула исходные данные");
    }

    @Test
    void testWithAllZeros() {
        byte[] key = new byte[8];
        byte[] plaintext = new byte[8];

        byte[] ciphertext = des.encode(plaintext, key);
        byte[] expected = hexToBytes("8CA64DE9C1B123A7");

        assertArrayEquals(expected, ciphertext,
                "Шифрование нулевого блока дало неверный результат");

        byte[] decrypted = des.decode(ciphertext, key);
        assertArrayEquals(plaintext, decrypted,
                "Дешифровка нулевого блока дала неверный результат");
    }

    @Test
    void testWithYourSpecificKey() {
        byte[] key = {(byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0xC4, (byte)0xC8, (byte)0xC0,
                (byte)0xCD, (byte)0xC0};

        byte[] plaintext = "DES_TEST".getBytes(StandardCharsets.UTF_8);

        byte[] ciphertext = des.encode(plaintext, key);

        assertNotEquals(plaintext, ciphertext,
                "Шифрование не изменило данные");

        assertEquals(8, ciphertext.length,
                "Размер шифроблока должен быть 8 байт");

        byte[] decrypted = des.decode(ciphertext, key);
        assertArrayEquals(plaintext, decrypted,
                "Дешифровка не вернула исходные данные");
    }

    @Test
    void testDoubleEncryption() {
        byte[] key = hexToBytes("0102030405060708");
        byte[] plaintext = "TEST1234".getBytes(StandardCharsets.UTF_8);

        byte[] ciphertext1 = des.encode(plaintext, key);
        byte[] ciphertext2 = des.encode(plaintext, key);

        assertArrayEquals(ciphertext1, ciphertext2,
                "Двойное шифрование с тем же ключом дало разные результаты");

        byte[] decrypted = des.decode(ciphertext1, key);
        assertArrayEquals(plaintext, decrypted,
                "Дешифровка не вернула исходные данные");
    }

    @Test
    void testDeterministicEncryption() {
        byte[] key = hexToBytes("AABBCCDDEEFF0011");
        byte[] plaintext = "ABCDEFGH".getBytes(StandardCharsets.UTF_8);

        byte[] ciphertext1 = des.encode(plaintext, key);
        byte[] ciphertext2 = des.encode(plaintext, key);

        assertArrayEquals(ciphertext1, ciphertext2,
                "Шифрование не детерминировано - разные результаты для одинаковых входов");

        byte[] decrypted = des.decode(ciphertext1, key);
        assertArrayEquals(plaintext, decrypted,
                "Дешифровка не вернула исходные данные");
    }

    @Test
    void testWithFileInput() throws Exception {
        byte[] testData = "8BYTESTX".getBytes(StandardCharsets.UTF_8);
        Path testFile = Files.createTempFile("des-test", ".txt");
        Files.write(testFile, testData);

        byte[] key = {(byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0xC4, (byte)0xC8, (byte)0xC0,
                (byte)0xCD, (byte)0xC0};

        byte[] fileContent = Files.readAllBytes(testFile);
        byte[] ciphertext = des.encode(fileContent, key);

        assertEquals(8, ciphertext.length);
        assertNotEquals(testData, ciphertext);

        byte[] decrypted = des.decode(ciphertext, key);
        assertArrayEquals(testData, decrypted,
                "Дешифровка файла не вернула исходные данные");

        Files.deleteIfExists(testFile);
    }

    @Test
    void testBoundaryValues() {
        byte[] key = hexToBytes("FFFFFFFFFFFFFFFF");
        byte[] maxBlock = hexToBytes("FFFFFFFFFFFFFFFF");

        byte[] ciphertext = des.encode(maxBlock, key);

        assertNotNull(ciphertext);
        assertEquals(8, ciphertext.length);

        byte[] decrypted = des.decode(ciphertext, key);
        assertArrayEquals(maxBlock, decrypted,
                "Дешифровка граничных значений дала неверный результат");
    }


}
