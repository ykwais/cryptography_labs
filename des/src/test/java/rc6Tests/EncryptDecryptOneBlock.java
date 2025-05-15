package rc6Tests;

import lombok.extern.slf4j.Slf4j;
import org.example.interfaces.EncryptorDecryptorSymmetric;
import org.example.rc6.RC6;
import org.example.rc6.enums.RC6KeyLength;
import org.junit.jupiter.api.Test;

import static org.example.utils.ToView.bytesToHex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Slf4j
class EncryptDecryptOneBlock {

    @Test
    void testEncryptDecryptOneBlock() {
        byte[] key = new byte[16];
        for (int i = 0; i < 16; i++) {
            key[i] = 0x00;
        }
        EncryptorDecryptorSymmetric cipher = new RC6(RC6KeyLength.KEY_128, key);
        byte[] plain = new byte[16];
        for (int i = 0; i < 16; i++) {
            plain[i] = 0x00;
        }
        byte[] cipherText = cipher.encrypt(plain);
        log.info("cipherText: {}", bytesToHex(cipherText));
        byte[] text = cipher.decrypt(cipherText);
        log.info("text: {}", bytesToHex(text));
        assertNotEquals(text, plain);
    }
}
