package ru.sibsutis.cryptomethods.core.math;

import java.math.BigInteger;

public class PowerMod {
    public static BigInteger calculate(BigInteger a, BigInteger x, BigInteger p) {
        if (p.equals(BigInteger.ONE)) return BigInteger.ZERO;
        if (x.equals(BigInteger.ZERO)) return BigInteger.ONE;

        int t = x.bitLength();

        BigInteger[] series = new BigInteger[t];
        series[0] = a.mod(p);
        for (int i = 1; i < t; i++) {
            series[i] = series[i-1].multiply(series[i-1]).mod(p);
        }

        String binaryX = x.toString(2);

        if (binaryX.length() != t) {
            t = binaryX.length();
        }

        BigInteger result = BigInteger.ONE;
        for (int i = 0; i < t; i++) {
            if (binaryX.charAt(t - 1 - i) == '1') {
                result = result.multiply(series[i]).mod(p);
            }
        }

        return result;
    }
}
