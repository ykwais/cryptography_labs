import org.example.attacks.AttackWiener;
import org.example.models.ResultWiener;
import org.example.rsa.Rsa;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class AttackWienerTests {
    @Test
    void testWienerAttackVulnerableKey2() {
        BigInteger p = new BigInteger("86969");
        BigInteger q = new BigInteger("86981");
        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger d = BigInteger.valueOf(3);
        BigInteger e = d.modInverse(phi);

        AttackWiener attack = new AttackWiener();
        ResultWiener result = attack.calculate(e, n);

        assertNotNull(result);
        assertEquals(d, result.d());
    }

    @Test
    void testWienerAttackFail() {
        Rsa service = new Rsa(Rsa.TestType.MILLER_RABIN, 1024, 0.99);
        BigInteger e = service.getPairKeys().getKey().e();
        BigInteger n = service.getPairKeys().getValue().n();

        AttackWiener attack = new AttackWiener();
        ResultWiener result = attack.calculate(e, n);

        assertNull(result);
    }
}
