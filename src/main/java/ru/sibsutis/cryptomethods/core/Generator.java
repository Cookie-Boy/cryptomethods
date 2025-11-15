package ru.sibsutis.cryptomethods.core;

import ru.sibsutis.cryptomethods.core.math.FermatTest;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Generator {
    private static final int BIT_LENGTH = 512;
    private static final SecureRandom random = new SecureRandom();

    public static BigInteger generateRandomBigInteger() {
        return new BigInteger(BIT_LENGTH, random).mod(BigInteger.valueOf((long) 1e10));
    }

    public static BigInteger generateRandomBigInteger(BigInteger max) {
        return new BigInteger(BIT_LENGTH, random).mod(max);
    }

    public static BigInteger generateRandomBigInteger(BigInteger min, BigInteger max) {
        return new BigInteger(BIT_LENGTH, random).mod(max.subtract(min)).add(min);
    }

    public static BigInteger generatePrimeNumber(BigInteger k, BigInteger min, BigInteger max) {
        return generatePrimeNumber(k.intValue(), min, max);
    }

    public static BigInteger generatePrimeNumber(int k, BigInteger min, BigInteger max) {
        BigInteger candidate;

        do {
            candidate = new BigInteger(BIT_LENGTH, random).mod(max.subtract(min)).add(min);
            if (!candidate.testBit(0)) {
                candidate = candidate.setBit(0);
            }
        } while (!FermatTest.check(candidate, k));

        return candidate;
    }

    public static BigInteger generatePrimeNumber(BigInteger k) {
        return generatePrimeNumber(k.intValue());
    }

    public static BigInteger generatePrimeNumber(int k) {
        BigInteger candidate;

        do {
            candidate = new BigInteger(BIT_LENGTH, random).mod(BigInteger.valueOf((long) 1e10));
            if (!candidate.testBit(0)) {
                candidate = candidate.setBit(0);
            }
        } while (!FermatTest.check(candidate, k));

        return candidate;
    }

    public static BigInteger generatePrimeWithBitLength(int bitLength) {
        if (bitLength < 2) {
            throw new IllegalArgumentException("Bit length must be at least 2");
        }

        BigInteger prime;
        int certainty = 100;

        do {
            prime = new BigInteger(bitLength, random);
            prime = prime.setBit(bitLength - 1);
            prime = prime.setBit(0); // устанавливаем младший бит в 1

        } while (!prime.isProbablePrime(certainty));

        return prime;
    }
}
