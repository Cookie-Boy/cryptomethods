package ru.sibsutis.cryptomethods.core.math;

import java.math.BigInteger;
import java.util.Random;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readBigInt;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readInt;

public class FermatTest {
    private static final Random random = new Random();

    public static BigInteger[] handle() {
        System.out.println("\n=== FERMAT'S PRIMALITY TEST ===");
        System.out.println("1. Check if a number is prime");
        System.out.println("2. Generate a prime number");
        System.out.print("Select an option (1-2): ");

        int choice = readInt();

        BigInteger number, k;
        switch (choice) {
            case 1:
                number = readBigInt("Enter a number to check");
                k = readBigInt("Enter test quantity (k)");
                break;
            case 2:
                k = readBigInt("Enter test quantity (k)");
                number = generatePrimeNumber(k);
                System.out.println("Generated prime number: " + number);
                System.out.println("Bit length: " + number.bitLength() + " bits");
                break;
            default:
                System.out.println("Wrong choice.");
                return null;
        }

        return new BigInteger[] { number, k };
    }

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
