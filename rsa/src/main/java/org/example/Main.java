package org.example;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

import static org.example.stateless.Math.gcd;

@Slf4j
public class Main {
    public static void main(String[] args) {
        log.info("Hello World!");
        log.info(gcd(new BigInteger("-15"), new BigInteger("20")).toString());
    }
}