package ru.sibsutis.cryptomethods;

import ru.sibsutis.cryptomethods.utilities.*;

import java.math.BigInteger;

public class CryptoLibrary {

    public static void start() {
        while (true) {
            System.out.println("\n=== CRYPTOGRAPHIC LIBRARY (512-bit) ===");
            System.out.println("1. Fast exponentiation by modulo");
            System.out.println("2. Fermat's Primality Test");
            System.out.println("3. Generalized Euclidean Algorithm");
            System.out.println("4. Baby's Step, Giant's Step");
            System.out.println("5. Diffi Hellman");
            System.out.println("0. Exit");
            System.out.print("Select an option (0-5): ");

            int choice = InputHandler.getIntInput();

            BigInteger[] args;
            BigInteger a, x, p, result;

            switch (choice) {
                case 1:
                    args = InputHandler.handlePowerMod();
                    if (args == null) break;
                    a = args[0]; x = args[1]; p = args[2];
                    result = PowerMod.calculate(a, x, p);
                    System.out.println("Result: " + a + "^" + x + " mod " + p + " = " + result);
                    break;
                case 2:
                    args = InputHandler.handleFermatTest();
                    if (args == null) break;
                    boolean isPrime = FermatTest.check(args[0], args[1]);
                    System.out.println("Number " + args[0] + (isPrime ? " is prime" : " is NOT prime"));
                    break;
                case 3:
                    args = InputHandler.handleExtendedEuclidean();
                    if (args == null) break;
                    EuclideanResult euclideanResult = ExtendedEuclidean.calculate(args[0], args[1]);
                    System.out.println("GCD(" + args[0] + ", " + args[1] + ") = " + euclideanResult.getGcd());
                    System.out.println("Coefficients: x = " + euclideanResult.getX() + ", y = " + euclideanResult.getY());

                    BigInteger check = args[0].multiply(euclideanResult.getX()).add(args[1].multiply(euclideanResult.getY()));
                    System.out.println("Checking: " + args[0] + "*" + euclideanResult.getX() + " + " + args[1] + "*" + euclideanResult.getY() + " = " + check);
                    break;
                case 4:
                    args = InputHandler.handleBabyAndGiantStep();
                    if (args == null) break;
                    System.out.println(args[0] + "^x * mod " + args[1] + " = " + args[2]);
                    x = BabyAndGiantStep.calculate(args[0], args[1], args[2]);
                    System.out.println("x = " + x);
                    break;
                case 5:
                    args = InputHandler.handleDiffHellman();
                    if (args == null) break;
                    BigInteger sharedKey = DiffHellman.calculate(args[0], args[1], args[2], args[3]);
                    System.out.println("result = " + sharedKey);
                    break;
                case 0:
                    System.out.println("Exit...");
                    return;
                default:
                    System.out.println("Wrong choice. Try again.");
            }
        }
    }
}