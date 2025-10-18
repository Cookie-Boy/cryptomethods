package ru.sibsutis.cryptomethods.core.math;

import java.math.BigInteger;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.core.Generator.generateRandomBigInteger;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readBigInt;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readInt;

public class PowerMod {
    public static BigInteger[] handle() {
        System.out.println("\n=== FAST EXPONENTIATION BY MODULO ===");
        System.out.println("1. Enter parameters manually");
        System.out.println("2. Generate parameters");
        System.out.print("Select an option (1-2): ");

        int choice = readInt();

        BigInteger a, x, p;
        switch (choice) {
            case 1:
                a = readBigInt("Enter base (a)");
                x = readBigInt("Enter degree (x)");
                p = readBigInt("Enter modulus (p)");
                break;
            case 2:
                a = generateRandomBigInteger();
                x = generateRandomBigInteger();
                p = generatePrimeNumber(100);
                System.out.println("Generated parameters:");
                System.out.println("a = " + a);
                System.out.println("x = " + x);
                System.out.println("p = " + p);
                break;
            default:
                System.out.println("Wrong choice.");
                return null;
        }

        return new BigInteger[] { a, x, p };
    }

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
