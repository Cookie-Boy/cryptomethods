package ru.sibsutis.cryptomethods.utilities;

import ru.sibsutis.cryptomethods.methods.PowerMod;

import java.math.BigInteger;
import java.util.Scanner;

import static ru.sibsutis.cryptomethods.utilities.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.utilities.Generator.generateRandomBigInteger;

public class InputHandler {
    private static final Scanner scanner = new Scanner(System.in);

    public static BigInteger[] handlePowerMod() {
        System.out.println("\n=== FAST EXPONENTIATION BY MODULO ===");
        System.out.println("1. Enter parameters manually");
        System.out.println("2. Generate parameters");
        System.out.print("Select an option (1-2): ");

        int choice = getIntInput();

        BigInteger a, x, p;
        switch (choice) {
            case 1:
                System.out.print("Enter base (a): ");
                a = getBigIntegerInput();
                System.out.print("Enter degree (x): ");
                x = getBigIntegerInput();
                System.out.print("Enter modulus (p): ");
                p = getBigIntegerInput();
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

    public static BigInteger[] handleFermatTest() {
        System.out.println("\n=== FERMAT'S PRIMALITY TEST ===");
        System.out.println("1. Check if a number is prime");
        System.out.println("2. Generate a prime number");
        System.out.print("Select an option (1-2): ");

        int choice = getIntInput();

        BigInteger number, k;
        switch (choice) {
            case 1:
                System.out.print("Enter a number to check: ");
                number = getBigIntegerInput();
                System.out.print("Enter test quantity (k): ");
                k = getBigIntegerInput();
                break;
            case 2:
                System.out.print("Enter test quantity (k): ");
                k = getBigIntegerInput();
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

    public static BigInteger[] handleExtendedEuclidean() {
        System.out.println("\n=== GENERALIZED EUCLIDEAN ALGORITHM ===");
        System.out.println("1. Enter numbers a and b");
        System.out.println("2. Generate numbers a and b");
        System.out.println("3. Generate prime numbers a and b");
        System.out.print("Select an option (1-3): ");

        int choice = getIntInput();

        BigInteger a, b;
        int k;
        switch (choice) {
            case 1:
                System.out.print("Enter number a: ");
                a = getBigIntegerInput();
                System.out.print("Enter number b: ");
                b = getBigIntegerInput();
                break;
            case 2:
                a = generateRandomBigInteger();
                b = generateRandomBigInteger();
                System.out.println("Generated numbers:");
                System.out.println("a = " + a);
                System.out.println("b = " + b);
                break;
            case 3:
                System.out.print("Enter test quantity (k): ");
                k = getIntInput();
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

    public static BigInteger[] handleBabyAndGiantStep() {
        System.out.println("\n=== BABY'S AND GIANT'S STEP ===");
        System.out.println("1. Enter numbers 'a', 'p' and 'y'");
        System.out.println("2. Generate numbers 'a', 'p' and 'y'");
        System.out.println("3. Special");
        System.out.print("Select an option (1-3): ");

        int choice = getIntInput();

        BigInteger a, p, y, x = null;
        switch (choice) {
            case 1:
                System.out.print("Enter number a: ");
                a = getBigIntegerInput();
                System.out.print("Enter number p: ");
                p = getBigIntegerInput();
                System.out.print("Enter number y: ");
                y = getBigIntegerInput();
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

    public static BigInteger[] handleDiffHellman() {
        System.out.println("\n=== DIFIFIFI HELLMAN ===");
        System.out.println("1. Enter numbers 'p', 'g', 'Xa', 'Xb'");
        System.out.println("2. Generate numbers 'p', 'g', 'Xa', 'Xb'");
        System.out.print("Select an option (1-2): ");

        int choice = getIntInput();

        BigInteger q, p, g, xA, xB;
        switch (choice) {
            case 1:
                System.out.print("Enter number p: ");
                p = getBigIntegerInput();
                System.out.print("Enter number g: ");
                g = getBigIntegerInput();
                System.out.print("Enter number Xa: ");
                xA = getBigIntegerInput();
                System.out.print("Enter number Xb: ");
                xB = getBigIntegerInput();
                System.out.println("You entered:");
                break;
            case 2:
                do {
                    q = generatePrimeNumber(50);
                    p = q.multiply(BigInteger.TWO).add(BigInteger.ONE);
                } while (!FermatTest.check(p, 50));

                for (g = BigInteger.TWO; g.compareTo(p.subtract(BigInteger.ONE)) < 0; g = g.add(BigInteger.ONE)) {
                    if (PowerMod.calculate(g, q, p).compareTo(BigInteger.ONE) != 0) {
                        break;
                    }
                }
                xA = generateRandomBigInteger();
                xB = generateRandomBigInteger();
                System.out.println("Generated values:");
                break;
            default:
                System.out.println("Wrong choice.");
                return null;
        }

        return new BigInteger[] { p, g, xA, xB };
    }

    private static BigInteger getBigIntegerInput() {
        while (true) {
            try {
                System.out.print("Enter number: ");
                String input = scanner.nextLine();
                return new BigInteger(input);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid integer");
            }
        }
    }

    public static int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter an integer: ");
            scanner.next();
        }
        int result = scanner.nextInt();
        scanner.nextLine();
        return result;
    }
}
