package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.rsa.Rsa;

import java.math.BigInteger;


@Slf4j
public class Main {
    public static void main(String[] args) {
        Rsa rsa = new Rsa(Rsa.TestType.MILLER_RABIN, 1024, 0.999999);//48
        BigInteger message = new BigInteger("21474836485856767456464564765111111");
        log.info("bit length of message : {}", message.bitLength());
        BigInteger cipher = rsa.encrypt(message);
        BigInteger decrypted = rsa.decrypt(cipher);

        log.info("origin: {}", message);
        log.info("cipher: {}", cipher);

        log.info("decrypted: {}", decrypted);

        if (message.equals(decrypted)) {
            log.info("equals");
        } else {
            log.info("not equals");
        }

    }
}