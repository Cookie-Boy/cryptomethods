package ru.sibsutis.cryptomethods.methods;

import java.math.BigInteger;

public class El_GamalCypher {
    public static BigInteger[] Encrypt(BigInteger[] args, BigInteger message) {
        BigInteger[] pair = new BigInteger[2];
        pair[0] = PowerMod.calculate(args[1], args[2], args[0]);
        BigInteger Boobs_Secret = PowerMod.calculate(args[1], args[3], args[0]);
        pair[1] = PowerMod.calculate(Boobs_Secret, args[2], args[0]).multiply(message).mod(args[0]);
        return pair;
    }

    public static BigInteger Decrypt(BigInteger[] args, BigInteger[] pair) {
        BigInteger message = PowerMod.calculate(pair[0], args[0].subtract(BigInteger.ONE).subtract(args[3]), args[0]).multiply(pair[1]).mod(args[0]);
        return message;
    }
}
