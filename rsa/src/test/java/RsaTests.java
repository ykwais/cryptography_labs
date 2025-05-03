import org.example.rsa.Rsa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RsaTests {
    @Test
    void testKeyGenerationFermat() {
        testKeyGeneration(Rsa.TestType.FERMAT);
    }

    @Test
    void testKeyGenerationSolovayStrassen() {
        testKeyGeneration(Rsa.TestType.SOLOVAY_STRASSEN);
    }

    @Test
    void testKeyGenerationMillerRabin() {
        testKeyGeneration(Rsa.TestType.MILLER_RABIN);
    }

    private void testKeyGeneration(Rsa.TestType testType) {
        int bitLength = 1024;
        double chance = 0.99;
        Rsa rsa = new Rsa(testType, bitLength, chance);

        assertNotNull(rsa.getPairKeys());
        assertNotNull(rsa.getPairKeys().getKey());
        assertNotNull(rsa.getPairKeys().getValue());


        BigInteger m = new BigInteger("123456789");
        BigInteger c = rsa.encrypt(m);
        BigInteger decrypted = rsa.decrypt(c);
        assertEquals(m, decrypted);
    }


    @ParameterizedTest
    @EnumSource(Rsa.TestType.class)
    void testEncryptionDecryption(Rsa.TestType testType) {
        Rsa rsa = new Rsa(testType, 1024, 0.99);
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            BigInteger m = new BigInteger(1000, random);
            BigInteger c = rsa.encrypt(m);
            BigInteger decrypted = rsa.decrypt(c);
            assertEquals(m, decrypted);
        }

        BigInteger n = rsa.getPairKeys().getKey().n();
        BigInteger zero = BigInteger.ZERO;
        BigInteger maxMessage = n.subtract(BigInteger.ONE);

        assertEquals(zero, rsa.decrypt(rsa.encrypt(zero)));
        assertEquals(maxMessage, rsa.decrypt(rsa.encrypt(maxMessage)));

        BigInteger tooBig = n.multiply(BigInteger.TWO);
        assertThrows(IllegalArgumentException.class, () -> rsa.encrypt(tooBig));
    }

}
