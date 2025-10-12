package ru.sibsutis.cryptomethods.io;

import ru.sibsutis.cryptomethods.core.Generator;
import ru.sibsutis.cryptomethods.core.math.ExtEuclid;
import ru.sibsutis.cryptomethods.core.math.FermatTest;
import ru.sibsutis.cryptomethods.core.math.PowerMod;

import java.math.BigInteger;
import java.util.Scanner;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.core.Generator.generateRandomBigInteger;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readBigInt;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readInt;

public class InputHandler {
    private static final Scanner scanner = new Scanner(System.in);

    public static BigInteger[] handlePowerMod() {
        System.out.println("\n=== FAST EXPONENTIATION BY MODULO ===");
        System.out.println("1. Enter parameters manually");
        System.out.println("2. Generate parameters");
        System.out.print("Select an option (1-2): ");

        int choice = readInt();

        BigInteger a, x, p;
        switch (choice) {
            case 1:
                System.out.print("Enter base (a): ");
                a = readBigInt();
                System.out.print("Enter degree (x): ");
                x = readBigInt();
                System.out.print("Enter modulus (p): ");
                p = readBigInt();
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

        int choice = readInt();

        BigInteger number, k;
        switch (choice) {
            case 1:
                System.out.print("Enter a number to check: ");
                number = readBigInt();
                System.out.print("Enter test quantity (k): ");
                k = readBigInt();
                break;
            case 2:
                System.out.print("Enter test quantity (k): ");
                k = readBigInt();
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

        int choice = readInt();

        BigInteger a, b;
        int k;
        switch (choice) {
            case 1:
                System.out.print("Enter number a: ");
                a = readBigInt();
                System.out.print("Enter number b: ");
                b = readBigInt();
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
                k = readInt();
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

    public static BigInteger[] handleDiffHellman() {
        System.out.println("\n=== DIFIFIFI HELLMAN ===");
        System.out.println("1. Enter numbers 'p', 'g', 'Xa', 'Xb'");
        System.out.println("2. Generate numbers 'p', 'g', 'Xa', 'Xb'");
        System.out.print("Select an option (1-2): ");

        int choice = readInt();

        BigInteger q, p, g, xA, xB;
        switch (choice) {
            case 1:
                System.out.print("Enter number p: ");
                p = readBigInt();
                System.out.print("Enter number g: ");
                g = readBigInt();
                System.out.print("Enter number Xa: ");
                xA = readBigInt();
                System.out.print("Enter number Xb: ");
                xB = readBigInt();
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

    public static BigInteger[] handleShamir() {
        System.out.println("\n=== SHAMIR ENCRYPTION ===");
        System.out.println("1. Enter numbers 'p', 'Ca', 'Cb''");
        System.out.println("2. Generate numbers 'P', 'Ca', 'Cb'");
        System.out.print("Select an option (1-2): ");

        int choice = readInt();

        BigInteger p, cA, cB, dA, dB;
        switch (choice) {
            case 1:
                System.out.print("Enter number p: ");
                do {
                    p = readBigInt();
                } while(!FermatTest.check(p, 100));
                do {
                    System.out.print("Enter number Ca: ");
                    cA = readBigInt();
                } while(ExtEuclid.calculate(p.subtract(BigInteger.ONE), cA)
                        .getGcd().compareTo(BigInteger.ONE) != 0);
                do {
                    System.out.print("Enter number Cb: ");
                    cB = readBigInt();
                } while(ExtEuclid.calculate(p.subtract(BigInteger.ONE), cB)
                        .getGcd().compareTo(BigInteger.ONE) != 0);
                System.out.println("You entered:");
                break;
            case 2:
                p = generatePrimeNumber(100);
                do {
                    cA = generateRandomBigInteger(new BigInteger("0"), p);
                } while(ExtEuclid.calculate(p.subtract(BigInteger.ONE), cA)
                        .getGcd().compareTo(BigInteger.ONE) != 0);

                do {
                    cB = generateRandomBigInteger(new BigInteger("0"), p);
                } while(ExtEuclid.calculate(p.subtract(BigInteger.ONE), cB)
                        .getGcd().compareTo(BigInteger.ONE) != 0);
                System.out.println("Generated values:");
                break;
            default:
                System.out.println("Wrong choice.");
                return null;
        }
        dA = ExtEuclid.calculate(p.subtract(BigInteger.ONE), cA).getY();
        dB = ExtEuclid.calculate(p.subtract(BigInteger.ONE), cB).getY();
        dA = dA.signum() < 0? dA.add(p.subtract(BigInteger.ONE)) : dA;
        dB = dB.signum() < 0? dB.add(p.subtract(BigInteger.ONE)) : dB;
        System.out.println("p =" + p + " Ca = " + cA + " Cb = " + cB + " Da = " + dA + " Db = " + dB);
        return new BigInteger[] { p, cA, cB, dA, dB};
    }

    public static BigInteger[] handleElGamal() {
        System.out.println("\n=== El'Gamal ===");
        System.out.println("1. Enter numbers 'p', 'g', 'Xa', 'Xb'");
        System.out.println("2. Generate numbers 'p', 'g', 'Xa', 'Xb'");
        System.out.print("Select an option (1-2): ");

        int choice = readInt();

        BigInteger q, p, g, xA, xB;
        switch (choice) {
            case 1:
                System.out.print("Enter number p: ");
                p = readBigInt();
                System.out.print("Enter number g: ");
                g = readBigInt();
                do {
                    System.out.print("Enter number Xa: ");
                    xA = readBigInt();
                } while(xA.compareTo(p) > 0);
                do {
                    System.out.print("Enter number Xb: ");
                    xB = readBigInt();
                } while(xB.compareTo(p) > 0);
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
                xA = generateRandomBigInteger(BigInteger.ONE, p.subtract(BigInteger.ONE));
                xB = generateRandomBigInteger(BigInteger.ONE, p.subtract(BigInteger.ONE));
                System.out.println("Generated values:");
                System.out.println("p = " + p + ", g = " + g + ", Xa = " + xA + ", Xb = " + xB);
                break;
            default:
                System.out.println("Wrong choice.");
                return null;
        }

        return new BigInteger[] { p, g, xA, xB };
    }

    public static BigInteger[] handleRSA() {
        System.out.println("\n=== RSA Cypher ===");
        System.out.println("1. Enter numbers 'p', 'q'");
        System.out.println("2. Generate numbers 'p', 'q'");
        System.out.print("Select an option (1-2): ");

        int choice = readInt();

        BigInteger q, p, N, f, d, c;
        switch (choice) {
            case 1:
                do {
                    System.out.print("Enter number p: ");
                    p = readBigInt();
                } while(!FermatTest.check(p, 100));
                do {
                    System.out.print("Enter number q: ");
                    q = readBigInt();
                } while(!FermatTest.check(q, 100));
                System.out.println("You entered: p = " + p + " q = " + q);
                break;
            case 2:
                q = generatePrimeNumber(50);
                p = generatePrimeNumber(50);
                System.out.println("p = " + p + ", q = " + q);
                break;
            default:
                System.out.println("Wrong choice.");
                return null;
        }
        N = p.multiply(q);
        f = N.subtract(p).subtract(q).add(BigInteger.ONE);
        do {
            d = Generator.generateRandomBigInteger(f);
        } while(ExtEuclid.calculate(f, d).getGcd().compareTo(BigInteger.ONE) != 0);
        c = ExtEuclid.calculate(f, d).getY();
        c = c.signum() < 0? c.add(f.subtract(BigInteger.ONE)) : c;
        return new BigInteger[] {N, d, c};
    }
}
