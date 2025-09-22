package ru.sibsutis.cryptomethods.utilities;

import java.math.BigInteger;

public class Generator {
    private static final int BIT_LENGTH = 512;

    public static BigInteger generateRandomBigInteger() {
        return new BigInteger(BIT_LENGTH, FermatTest.random).mod(BigInteger.valueOf((long) 1e9));
    }

    public static BigInteger generatePrimeNumber(BigInteger k) {
        return generatePrimeNumber(k.intValue());
    }

    public static BigInteger generatePrimeNumber(int k) {
        BigInteger candidate;

        do {
            candidate = new BigInteger(BIT_LENGTH, FermatTest.random).mod(BigInteger.valueOf((long) 1e9));
            if (!candidate.testBit(0)) {
                candidate = candidate.setBit(0);
            }
        } while (!FermatTest.check(candidate, k));

        return candidate;
    }
}
