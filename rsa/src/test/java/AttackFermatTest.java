import javafx.util.Pair;
import org.example.attacks.AttackFermat;
import org.example.models.OpenKey;
import org.example.rsa.Rsa;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class AttackFermatTest {
    private final AttackFermat attack = new AttackFermat();

    @Test
    void testClosePrimes() {
        BigInteger p = new BigInteger("10007");
        BigInteger q = new BigInteger("10009");
        BigInteger n = p.multiply(q);
        BigInteger e = BigInteger.valueOf(65537);

        Pair<BigInteger, BigInteger> result = attack.calculate(e, n);

        BigInteger expectedPhi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        assertEquals(expectedPhi, result.getValue());

        BigInteger expectedD = e.modInverse(expectedPhi);
        assertEquals(expectedD, result.getKey());
    }

    @Test
    void testVeryClosePrimes() {

        BigInteger p = new BigInteger("1000003");
        BigInteger q = new BigInteger("1000033");
        BigInteger n = p.multiply(q);
        BigInteger e = BigInteger.valueOf(65537);

        Pair<BigInteger, BigInteger> result = attack.calculate(e, n);

        BigInteger expectedPhi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        assertEquals(expectedPhi, result.getValue());

        BigInteger expectedD = e.modInverse(expectedPhi);
        assertEquals(expectedD, result.getKey());
    }



    @Test
    void testNotClosePrimesShouldFail() {

        BigInteger p = new BigInteger("2000000000000000003");
        BigInteger q = new BigInteger("1000000000000000003");
        BigInteger n = p.multiply(q);
        BigInteger e = BigInteger.valueOf(65537);


        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            attack.calculate(e, n);
        }, "Атака на далекие простые числа должна занимать много времени"); // должно падать!!!
    }

    @Test
    void testMyRSA() {

        Rsa rsa = new Rsa(Rsa.TestType.MILLER_RABIN, 128, 0.999);

        OpenKey open = rsa.getPairKeys().getKey();

        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            attack.calculate(open.e(), open.n());
        }, "Атака на далекие простые числа должна занимать много времени"); // должно падать!!!
    }


}
