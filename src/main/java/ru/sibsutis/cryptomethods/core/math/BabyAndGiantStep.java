package ru.sibsutis.cryptomethods.core.math;

import java.math.BigInteger;
import java.util.HashMap;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.core.Generator.generateRandomBigInteger;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readBigInt;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readInt;

public class BabyAndGiantStep {
    public static BigInteger[] handle() {
        System.out.println("\n=== BABY'S AND GIANT'S STEP ===");
        System.out.println("1. Enter numbers 'a', 'p' and 'y'");
        System.out.println("2. Generate numbers 'a', 'p' and 'y'");
        System.out.println("3. Special");
        System.out.print("Select an option (1-3): ");

        int choice = readInt();

        BigInteger a, p, y, x = null;
        switch (choice) {
            case 1:
                System.out.print("Enter number a: ");
                a = readBigInt();
                System.out.print("Enter number p: ");
                p = readBigInt();
                System.out.print("Enter number y: ");
                y = readBigInt();
                System.out.println("You entered:");
                break;
            case 2:
                a = generateRandomBigInteger();
                p = generatePrimeNumber(50);
                y = generateRandomBigInteger();
                System.out.println("Generated values:");
                break;
            case 3:
                a = generateRandomBigInteger();
                p = generatePrimeNumber(50);
                x = generateRandomBigInteger().mod(p.subtract(BigInteger.TWO.add(BigInteger.TWO))).add(BigInteger.TWO);
                System.out.println("Generated a = " + a + ", p = " + p + ", x = " + x);
                System.out.println("Calculating y...");
                y = PowerMod.calculate(a, x, p);
                System.out.println("y = " + y + ", calling baby and giant...");
                break;
            default:
                System.out.println("Wrong choice.");
                return null;
        }
        return new BigInteger[] { a, p, y, x };
    }

    public static BigInteger calculate(BigInteger a, BigInteger p, BigInteger y) {
        BigInteger m, k;
        m = p.sqrt().add(BigInteger.ONE);
        HashMap<BigInteger, BigInteger> babySteps = new HashMap<>();

        for (BigInteger i = BigInteger.ZERO; i.compareTo(m) < 0; i = i.add(BigInteger.ONE)) {
            BigInteger step = y.multiply(PowerMod.calculate(a, i, p)).mod(p);
            babySteps.put(step, i);
        }

        BigInteger babyIndex = null, giantIndex = null;
        for (BigInteger i = BigInteger.ONE; i.compareTo(m) <= 0; i = i.add(BigInteger.ONE)) {
            BigInteger step = PowerMod.calculate(a, i.multiply(m), p);
            babyIndex = babySteps.get(step);
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
