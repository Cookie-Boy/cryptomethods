package ru.sibsutis.cryptomethods.core.math;

import java.math.BigInteger;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.core.Generator.generateRandomBigInteger;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readBigInt;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readInt;

public class ExtEuclid {
    public static BigInteger[] handle() {
        System.out.println("\n=== GENERALIZED EUCLIDEAN ALGORITHM ===");
        System.out.println("1. Enter numbers a and b");
        System.out.println("2. Generate numbers a and b");
        System.out.println("3. Generate prime numbers a and b");
        System.out.print("Select an option (1-3): ");

        int choice = readInt();

        BigInteger a, b;
        int k;
        switch (choice) {
            case 1:
                a = readBigInt("Enter number a");
                b = readBigInt("Enter number b");
                break;
            case 2:
                a = generateRandomBigInteger();
                b = generateRandomBigInteger();
                System.out.println("Generated numbers:");
                System.out.println("a = " + a);
                System.out.println("b = " + b);
                break;
            case 3:
                k = readInt("Enter test quantity (k)");
                a = generatePrimeNumber(k);
                b = generatePrimeNumber(k);
                System.out.println("Generated prime numbers:");
                System.out.println("a = " + a);
                System.out.println("b = " + b);
                break;
            default:
                System.out.println("Wrong choice.");
                return null;
        }

        return new BigInteger[] { a, b };
    }

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
