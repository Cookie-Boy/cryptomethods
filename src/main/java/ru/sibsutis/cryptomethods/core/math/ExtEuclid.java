package ru.sibsutis.cryptomethods.core.math;

import java.math.BigInteger;

import static java.util.Collections.swap;

public class ExtEuclid {
    public static EuclidResult calculate(BigInteger a, BigInteger b) {
        if(a.compareTo(b) < 0) {
            BigInteger tmp = a;
            a = b;
            b = tmp;
        }
        EuclidResult u = new EuclidResult(a, BigInteger.ONE, BigInteger.ZERO);
        EuclidResult v = new EuclidResult(b, BigInteger.ZERO, BigInteger.ONE);

        while (!v.getGcd().equals(BigInteger.ZERO)) {
            BigInteger q = u.getGcd().divide(v.getGcd());
            EuclidResult t = new EuclidResult(
                    u.getGcd().mod(v.getGcd()),
                    u.getX().subtract(q.multiply(v.getX())),
                    u.getY().subtract(q.multiply(v.getY()))
            );
            u = v;
            v = t;
        }

        return u;
    }
}
