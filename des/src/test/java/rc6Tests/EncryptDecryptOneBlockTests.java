package rc6Tests;

import lombok.extern.slf4j.Slf4j;
import org.example.interfaces.EncryptorDecryptorSymmetric;
import org.example.rc6.RC6;
import org.example.rc6.enums.RC6KeyLength;
import org.junit.jupiter.api.Test;

import static org.example.utils.ToView.bytesToHex;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class EncryptDecryptOneBlockTests {

    @Test
    void testEncryptDecryptOneBlock1() {
        byte[] key = new byte[16];
        for (int i = 0; i < 16; i++) {
            key[i] = 0x00;
        }
        EncryptorDecryptorSymmetric cipher = new RC6(RC6KeyLength.KEY_128, key);
        byte[] plain = new byte[16];
        for (int i = 0; i < 16; i++) {
            plain[i] = 15;
        }
        plain[0] = 1;
        byte[] cipherText = cipher.encrypt(plain);
        log.info("cipherText: {}", bytesToHex(cipherText));
        byte[] text = cipher.decrypt(cipherText);
        log.info("text: {}", bytesToHex(text));
        assertArrayEquals(text, plain);
    }

    @Test
    void testEncryptDecryptOneBlock2() {
        byte[] key = new byte[]{
                (byte) 0x01, (byte) 0x23,(byte) 0x45,(byte) 0x67,
                (byte) 0x89,(byte) 0xab,(byte) 0xcd,(byte) 0xef,
                (byte) 0x01,(byte) 0x12,(byte) 0x23,(byte) 0x34,
                (byte) 0x45,(byte) 0x56,(byte) 0x67,(byte) 0x78
        };

        byte[] plain = new byte[]{
                (byte) 0x02, (byte) 0x13,(byte) 0x24,(byte) 0x35,
                (byte) 0x46,(byte) 0x57,(byte) 0x68,(byte) 0x79,
                (byte) 0x8a,(byte) 0x9b,(byte) 0xac,(byte) 0xbd,
                (byte) 0xce,(byte) 0xdf,(byte) 0xe0,(byte) 0xf1
        };

        EncryptorDecryptorSymmetric cipher = new RC6(RC6KeyLength.KEY_128, key);

        byte[] expectedCipher = new byte[]{
                (byte) 0x52, (byte) 0x4E,(byte) 0x19,(byte) 0x2F,
                (byte) 0x47,(byte) 0x15,(byte) 0xC6,(byte) 0x23,
                (byte) 0x1F,(byte) 0x51,(byte) 0xF6,(byte) 0x36,
                (byte) 0x7E,(byte) 0xA4,(byte) 0x3F,(byte) 0x18
        };

        byte[] cipherText = cipher.encrypt(plain);
        log.info("cipherText: {}", bytesToHex(cipherText));
        assertArrayEquals(expectedCipher, cipherText);
        byte[] text = cipher.decrypt(cipherText);
        log.info("text: {}", bytesToHex(text));
        assertArrayEquals(text, plain);
    }

    @Test
    void testEncryptDecryptOneBlock3() {
        byte[] key = new byte[24];
        for (int i = 0; i < 24; i++) {
            key[i] = 0x00;
        }
        EncryptorDecryptorSymmetric cipher = new RC6(RC6KeyLength.KEY_192, key);
        byte[] plain = new byte[16];
        for (int i = 0; i < 16; i++) {
            plain[i] = 0x00;
        }

        byte[] expectedCipher = new byte[]{
                (byte) 0x6c, (byte) 0xd6,(byte) 0x1b,(byte) 0xcb,
                (byte) 0x19,(byte) 0x0b,(byte) 0x30,(byte) 0x38,
                (byte) 0x4e,(byte) 0x8a,(byte) 0x3f,(byte) 0x16,
                (byte) 0x86,(byte) 0x90,(byte) 0xae,(byte) 0x82
        };


        byte[] cipherText = cipher.encrypt(plain);
        log.info("cipherText: {}", bytesToHex(cipherText));
        assertArrayEquals(expectedCipher, cipherText);
        byte[] text = cipher.decrypt(cipherText);
        log.info("text: {}", bytesToHex(text));
        assertArrayEquals(text, plain);
    }

    @Test
    void testEncryptDecryptOneBlock4() {

        byte[] key = new byte[]{
                (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67,
                (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef,
                (byte) 0x01, (byte) 0x12, (byte) 0x23, (byte) 0x34,
                (byte) 0x45, (byte) 0x56, (byte) 0x67, (byte) 0x78,
                (byte) 0x89, (byte) 0x9a, (byte) 0xab, (byte) 0xbc,
                (byte) 0xcd, (byte) 0xde, (byte) 0xef, (byte) 0xf0
        };

        byte[] plain = new byte[]{
                (byte) 0x02, (byte) 0x13, (byte) 0x24, (byte) 0x35,
                (byte) 0x46, (byte) 0x57, (byte) 0x68, (byte) 0x79,
                (byte) 0x8a, (byte) 0x9b, (byte) 0xac, (byte) 0xbd,
                (byte) 0xce, (byte) 0xdf, (byte) 0xe0, (byte) 0xf1
        };

        byte[] expectedCipher = new byte[]{
                (byte) 0x68, (byte) 0x83, (byte) 0x29, (byte) 0xd0,
                (byte) 0x19, (byte) 0xe5, (byte) 0x05, (byte) 0x04,
                (byte) 0x1e, (byte) 0x52, (byte) 0xe9, (byte) 0x2a,
                (byte) 0xf9, (byte) 0x52, (byte) 0x91, (byte) 0xd4
        };

        EncryptorDecryptorSymmetric cipher = new RC6(RC6KeyLength.KEY_192, key);

        byte[] cipherText = cipher.encrypt(plain);
        log.info("Ciphertext: {}", bytesToHex(cipherText));
        assertArrayEquals(expectedCipher, cipherText);

        byte[] decryptedText = cipher.decrypt(cipherText);
        log.info("Decrypted: {}", bytesToHex(decryptedText));
        assertArrayEquals(plain, decryptedText);
    }


    @Test
    void testEncryptDecryptOneBlock5() {

        byte[] key = new byte[32];

        byte[] plain = new byte[16];


        byte[] expectedCipher = new byte[]{
                (byte) 0x8f, (byte) 0x5f, (byte) 0xbd, (byte) 0x05,
                (byte) 0x10, (byte) 0xd1, (byte) 0x5f, (byte) 0xa8,
                (byte) 0x93, (byte) 0xfa, (byte) 0x3f, (byte) 0xda,
                (byte) 0x6e, (byte) 0x85, (byte) 0x7e, (byte) 0xc2
        };

        EncryptorDecryptorSymmetric cipher = new RC6(RC6KeyLength.KEY_256, key);

        byte[] cipherText = cipher.encrypt(plain);
        log.info("Ciphertext: {}", bytesToHex(cipherText));
        assertArrayEquals(expectedCipher, cipherText);

        byte[] decryptedText = cipher.decrypt(cipherText);
        log.info("Decrypted: {}", bytesToHex(decryptedText));
        assertArrayEquals(plain, decryptedText);
    }

    @Test
    void testEncryptDecryptOneBlock6() {

        byte[] key = new byte[]{
                (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67,
                (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef,
                (byte) 0x01, (byte) 0x12, (byte) 0x23, (byte) 0x34,
                (byte) 0x45, (byte) 0x56, (byte) 0x67, (byte) 0x78,
                (byte) 0x89, (byte) 0x9a, (byte) 0xab, (byte) 0xbc,
                (byte) 0xcd, (byte) 0xde, (byte) 0xef, (byte) 0xf0,
                (byte) 0x10, (byte) 0x32, (byte) 0x54, (byte) 0x76,
                (byte) 0x98, (byte) 0xba, (byte) 0xdc, (byte) 0xfe
        };

        byte[] plain = new byte[]{
                (byte) 0x02, (byte) 0x13, (byte) 0x24, (byte) 0x35,
                (byte) 0x46, (byte) 0x57, (byte) 0x68, (byte) 0x79,
                (byte) 0x8a, (byte) 0x9b, (byte) 0xac, (byte) 0xbd,
                (byte) 0xce, (byte) 0xdf, (byte) 0xe0, (byte) 0xf1
        };

        byte[] expectedCipher = new byte[]{
                (byte) 0xc8, (byte) 0x24, (byte) 0x18, (byte) 0x16,
                (byte) 0xf0, (byte) 0xd7, (byte) 0xe4, (byte) 0x89,
                (byte) 0x20, (byte) 0xad, (byte) 0x16, (byte) 0xa1,
                (byte) 0x67, (byte) 0x4e, (byte) 0x5d, (byte) 0x48
        };

        EncryptorDecryptorSymmetric cipher = new RC6(RC6KeyLength.KEY_256, key);

        byte[] cipherText = cipher.encrypt(plain);
        log.info("Ciphertext: {}", bytesToHex(cipherText));
        assertArrayEquals(expectedCipher, cipherText);

        byte[] decryptedText = cipher.decrypt(cipherText);
        log.info("Decrypted: {}", bytesToHex(decryptedText));
        assertArrayEquals(plain, decryptedText);
    }
}
