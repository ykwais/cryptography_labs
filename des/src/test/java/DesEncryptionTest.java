import org.example.des.Des;
import org.example.interfaces.impl.FiestelFunction;
import org.example.interfaces.impl.KeyExpansionImpl;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DesEncryptionOneBlockTest {


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
        Des des = new Des(key, new KeyExpansionImpl(), new FiestelFunction());
        byte[] plaintext = hexToBytes("0123456789ABCDEF");

        byte[] ciphertext = des.encrypt(plaintext);
        byte[] expected = hexToBytes("85E813540F0AB405");

        assertArrayEquals(expected, ciphertext,
                "Шифрование не соответствует NIST тестовому вектору");

        byte[] decrypted = des.decrypt(ciphertext);
        assertArrayEquals(plaintext, decrypted,
                "Дешифровка не вернула исходные данные");
    }

    @Test
    void testWithAllZeros() {
        byte[] key = new byte[8];
        Des des = new Des(key, new KeyExpansionImpl(), new FiestelFunction());

        byte[] plaintext = new byte[8];

        byte[] ciphertext = des.encrypt(plaintext);
        byte[] expected = hexToBytes("8CA64DE9C1B123A7");

        assertArrayEquals(expected, ciphertext,
                "Шифрование нулевого блока дало неверный результат");

        byte[] decrypted = des.decrypt(ciphertext);
        assertArrayEquals(plaintext, decrypted,
                "Дешифровка нулевого блока дала неверный результат");
    }

    @Test
    void testWithYourSpecificKey() {
        byte[] key = {(byte)0x00, (byte)0x00, (byte)0x00,
                (byte)0xC4, (byte)0xC8, (byte)0xC0,
                (byte)0xCD, (byte)0xC0};

        Des des = new Des(key, new KeyExpansionImpl(), new FiestelFunction());


        byte[] plaintext = "DES_TEST".getBytes(StandardCharsets.UTF_8);

        byte[] ciphertext = des.encrypt(plaintext);

        assertNotEquals(plaintext, ciphertext,
                "Шифрование не изменило данные");

        assertEquals(8, ciphertext.length,
                "Размер шифроблока должен быть 8 байт");

        byte[] decrypted = des.decrypt(ciphertext);
        assertArrayEquals(plaintext, decrypted,
                "Дешифровка не вернула исходные данные");
    }

    @Test
    void testDoubleEncryption() {
        byte[] key = hexToBytes("0102030405060708");
        Des des = new Des(key, new KeyExpansionImpl(), new FiestelFunction());

        byte[] plaintext = "TEST1234".getBytes(StandardCharsets.UTF_8);

        byte[] ciphertext1 = des.encrypt(plaintext);
        byte[] ciphertext2 = des.encrypt(plaintext);

        assertArrayEquals(ciphertext1, ciphertext2,
                "Двойное шифрование с тем же ключом дало разные результаты");

        byte[] decrypted = des.decrypt(ciphertext1);
        assertArrayEquals(plaintext, decrypted,
                "Дешифровка не вернула исходные данные");
    }

    @Test
    void testDeterministicEncryption() {
        byte[] key = hexToBytes("AABBCCDDEEFF0011");
        Des des = new Des(key, new KeyExpansionImpl(), new FiestelFunction());

        byte[] plaintext = "ABCDEFGH".getBytes(StandardCharsets.UTF_8);

        byte[] ciphertext1 = des.encrypt(plaintext);
        byte[] ciphertext2 = des.encrypt(plaintext);

        assertArrayEquals(ciphertext1, ciphertext2,
                "Шифрование не детерминировано - разные результаты для одинаковых входов");

        byte[] decrypted = des.decrypt(ciphertext1);
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

        Des des = new Des(key, new KeyExpansionImpl(), new FiestelFunction());


        byte[] fileContent = Files.readAllBytes(testFile);
        byte[] ciphertext = des.encrypt(fileContent);

        assertEquals(8, ciphertext.length);
        assertNotEquals(testData, ciphertext);

        byte[] decrypted = des.decrypt(ciphertext);
        assertArrayEquals(testData, decrypted,
                "Дешифровка файла не вернула исходные данные");

        Files.deleteIfExists(testFile);
    }

    @Test
    void testBoundaryValues() {
        byte[] key = hexToBytes("FFFFFFFFFFFFFFFF");
        Des des = new Des(key, new KeyExpansionImpl(), new FiestelFunction());

        byte[] maxBlock = hexToBytes("FFFFFFFFFFFFFFFF");

        byte[] ciphertext = des.encrypt(maxBlock);

        assertNotNull(ciphertext);
        assertEquals(8, ciphertext.length);

        byte[] decrypted = des.decrypt(ciphertext);
        assertArrayEquals(maxBlock, decrypted,
                "Дешифровка граничных значений дала неверный результат");
    }


}
