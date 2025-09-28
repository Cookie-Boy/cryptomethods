package ru.sibsutis.cryptomethods.utilities;

import java.math.BigInteger;

public class ExtendedEuclidean {
    public static EuclideanResult calculate(BigInteger a, BigInteger b) {
        // a должно быть больше b
        // если результат - отрицательное число, то добавляем к нему p - 1
        EuclideanResult u = new EuclideanResult(a, BigInteger.ONE, BigInteger.ZERO);
        EuclideanResult v = new EuclideanResult(b, BigInteger.ZERO, BigInteger.ONE);

        while (!v.getGcd().equals(BigInteger.ZERO)) {
            BigInteger q = u.getGcd().divide(v.getGcd());
            EuclideanResult t = new EuclideanResult(
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
