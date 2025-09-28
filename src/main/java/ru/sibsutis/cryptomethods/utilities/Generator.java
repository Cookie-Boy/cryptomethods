package ru.sibsutis.cryptomethods.utilities;

import java.math.BigInteger;

public class Generator {
    private static final int BIT_LENGTH = 512;

    public static BigInteger generateRandomBigInteger() {
        return new BigInteger(BIT_LENGTH, FermatTest.random).mod(BigInteger.valueOf((long) 1e10));
    }

    public static BigInteger generateRandomBigInteger(BigInteger min, BigInteger max) {
        return new BigInteger(BIT_LENGTH, FermatTest.random).mod(max.subtract(min)).add(min);
    }

    public static BigInteger generatePrimeNumber(BigInteger k, BigInteger min, BigInteger max) {
        return generatePrimeNumber(k.intValue(), min, max);
    }

    public static BigInteger generatePrimeNumber(int k, BigInteger min, BigInteger max) {
        BigInteger candidate;

        do {
            candidate = new BigInteger(BIT_LENGTH, FermatTest.random).mod(max.subtract(min)).add(min);
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
            candidate = new BigInteger(BIT_LENGTH, FermatTest.random).mod(BigInteger.valueOf((long) 1e10));
            if (!candidate.testBit(0)) {
                candidate = candidate.setBit(0);
            }
        } while (!FermatTest.check(candidate, k));

        return candidate;
    }
}
