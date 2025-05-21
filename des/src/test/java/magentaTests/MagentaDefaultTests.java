package magentaTests;

import lombok.extern.slf4j.Slf4j;
import org.example.interfaces.EncryptorDecryptorSymmetric;
import org.example.magenta.Magenta;
import org.example.magenta.enums.MagentaKeyLength;
import org.example.magenta.supply.GeneratorSBlock;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Arrays;

import static org.example.utils.ToView.bytesToHex;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
class MagentaDefaultTests {
    @Test
    void testSBoxInversionCorrectness() {

        GeneratorSBlock generator = new GeneratorSBlock((byte) 0x65);
        byte[] sBox = generator.getSBlock();

        System.out.println("S-Box in HEX format:");
        for (int i = 0; i < 32; i++) {

            for (int j = 0; j < 8; j++) {
                System.out.printf(Integer.toUnsignedLong(sBox[i * 8 + j] & 0xFF) + " ");
            }
            System.out.println();
        }

    }

    @Test
    void testEncryptDecryptOneBlock1() {
        byte[] key = new byte[16];
        for (int i = 0; i < 16; i++) {
            key[i] = 0x00;
        }
        EncryptorDecryptorSymmetric cipher = new Magenta(MagentaKeyLength.KEY_128, key);
        byte[] plain = new byte[16];
        for (int i = 0; i < 16; i++) {
            plain[i] = (byte) 0xff;
        }
        plain[0] = 1;
        byte[] cipherText = cipher.encrypt(plain);
        log.info("cipherText: {}", bytesToHex(cipherText));
        byte[] text = cipher.decrypt(cipherText);
        log.info("text: {}", bytesToHex(text));
        assertArrayEquals(text, plain);
    }

    @Test
    void testEncryptDecryptAllZeros() {
        byte[] key = new byte[16];
        byte[] plain = new byte[16];
        EncryptorDecryptorSymmetric cipher = new Magenta(MagentaKeyLength.KEY_128, key);
        byte[] cipherText = cipher.encrypt(plain);
        log.info("cipherText: {}", bytesToHex(cipherText));
        byte[] decrypted = cipher.decrypt(cipherText);
        assertArrayEquals(plain, decrypted);
    }

    @Test
    void testEncryptDecryptAllOnes() {
        byte[] key = new byte[16];
        Arrays.fill(key, (byte) 0xFF);
        byte[] plain = new byte[16];
        Arrays.fill(plain, (byte) 0xFF);
        EncryptorDecryptorSymmetric cipher = new Magenta(MagentaKeyLength.KEY_128, key);
        byte[] cipherText = cipher.encrypt(plain);
        byte[] decrypted = cipher.decrypt(cipherText);
        assertArrayEquals(plain, decrypted);
    }

    @Test
    void testKey192() {
        byte[] key = new byte[24];
        Arrays.fill(key, (byte) 0xAB);
        byte[] plain = "HelloMagenta1234".getBytes();
        log.info("plain: {}", bytesToHex(plain));
        EncryptorDecryptorSymmetric cipher = new Magenta(MagentaKeyLength.KEY_192, key);
        byte[] cipherText = cipher.encrypt(plain);
        byte[] decrypted = cipher.decrypt(cipherText);
        log.info("decrypted text: {}", bytesToHex(decrypted));
        assertArrayEquals(plain, decrypted);
    }

    @Test
    void testKey256() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        byte[] plain = "Test256BitKeyMag".getBytes();
        EncryptorDecryptorSymmetric cipher = new Magenta(MagentaKeyLength.KEY_256, key);
        byte[] cipherText = cipher.encrypt(plain);
        byte[] decrypted = cipher.decrypt(cipherText);
        assertArrayEquals(plain, decrypted);
    }

    @Test
    void testDifferentKeysProduceDifferentCiphertexts() {

        byte[] key1 = new byte[16];
        byte[] key2 = new byte[16];
        key2[0] = 0x01;
        byte[] plain = new byte[16];

        EncryptorDecryptorSymmetric cipher1 = new Magenta(MagentaKeyLength.KEY_128, key1);
        EncryptorDecryptorSymmetric cipher2 = new Magenta(MagentaKeyLength.KEY_128, key2);

        assertNotEquals(
                bytesToHex(cipher1.encrypt(plain)),
                bytesToHex(cipher2.encrypt(plain))
        );
    }


    @Test
    void testNonBlockSizeInput() {
        byte[] key = new byte[16];
        byte[] plain = "ShortText".getBytes();
        EncryptorDecryptorSymmetric cipher = new Magenta(MagentaKeyLength.KEY_128, key);
        assertThrows(IllegalArgumentException.class, () -> cipher.encrypt(plain));
    }
}
