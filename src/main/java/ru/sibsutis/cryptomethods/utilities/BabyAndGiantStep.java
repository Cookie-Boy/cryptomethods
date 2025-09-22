package ru.sibsutis.cryptomethods.utilities;

import java.math.BigInteger;
import java.util.HashMap;

public class BabyAndGiantStep {
    public static BigInteger calculate(BigInteger a, BigInteger p, BigInteger y) {
        BigInteger m, k;
//        m = p.sqrt().add(BigInteger.ONE);
        m = BigInteger.valueOf(5);
        k = BigInteger.valueOf(9);
        System.out.println("m = " + m);
        HashMap<BigInteger, BigInteger> babySteps = new HashMap<>();

        for (BigInteger i = BigInteger.ZERO; i.compareTo(m) < 0; i = i.add(BigInteger.ONE)) {
            BigInteger step = y.multiply(PowerMod.calculate(a, i, p)).mod(p);
            babySteps.put(step, i);
//            System.out.println("baby = " + step);
        }

        BigInteger babyIndex = null, giantIndex = null;
        for (BigInteger i = BigInteger.ONE; i.compareTo(k) <= 0; i = i.add(BigInteger.ONE)) {
            BigInteger step = PowerMod.calculate(a, i.multiply(m), p);
            babyIndex = babySteps.get(step);
//            System.out.println("giant = " + step);
            if (babyIndex != null) {
                giantIndex = i;
                break;
            }
        }

        if (giantIndex == null) {
            System.out.println("No solutions...");
            return null;
        }

        return giantIndex.multiply(m).subtract(babyIndex);
    }
}
