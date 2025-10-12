package ru.sibsutis.cryptomethods.core.math;

import java.math.BigInteger;
import java.util.Random;

public class FermatTest {
    static final Random random = new Random();

    public static boolean check(BigInteger p, BigInteger k) {
        return check(p, k.intValue());
    }

    public static boolean check(BigInteger p, int k) {
        if (p.equals(BigInteger.valueOf(2))) return true;
        if (p.compareTo(BigInteger.valueOf(2)) < 0 || !p.testBit(0)) return false;

        for (int i = 0; i < k; i++) {
            BigInteger a;
            do {
                a = new BigInteger(p.bitLength(), random);
            } while (a.compareTo(BigInteger.ONE) <= 0 || a.compareTo(p) >= 0);

            BigInteger gcd = a.gcd(p);
            if (!gcd.equals(BigInteger.ONE)) {
                return false;
            }

            BigInteger exponent = p.subtract(BigInteger.ONE);
            BigInteger result = PowerMod.calculate(a, exponent, p);

            if (!result.equals(BigInteger.ONE)) {
                return false;
            }
        }

        return true;
    }

}
