package ru.sibsutis.cryptomethods.app;

import ru.sibsutis.cryptomethods.core.math.EuclidResult;
import ru.sibsutis.cryptomethods.core.math.BabyAndGiantStep;
import ru.sibsutis.cryptomethods.core.math.ExtEuclid;
import ru.sibsutis.cryptomethods.core.math.FermatTest;
import ru.sibsutis.cryptomethods.core.math.PowerMod;
import ru.sibsutis.cryptomethods.algorithms.*;
import ru.sibsutis.cryptomethods.io.*;

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
            System.out.println("6. Shamir's cypher");
            System.out.println("7. El'Gamal's cypher");
            System.out.println("8. RSA cypher");
            System.out.println("0. Exit");
            System.out.print("Select an option (0-8): ");

            int choice = ConsoleInput.readInt();

            BigInteger[] args;
            BigInteger a, x, p, result;
            String fileName, encFileName;

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
                    EuclidResult euclidResult = ExtEuclid.calculate(args[0], args[1]);
                    System.out.println("GCD(" + args[0] + ", " + args[1] + ") = " + euclidResult.getGcd());
                    System.out.println("Coefficients: x = " + euclidResult.getX() + ", y = " + euclidResult.getY());

                    BigInteger check = args[0].multiply(euclidResult.getX()).add(args[1].multiply(euclidResult.getY()));
                    System.out.println("Checking: " + args[0] + "*" + euclidResult.getX() + " + " + args[1] + "*" + euclidResult.getY() + " = " + check);
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
                    BigInteger sharedKey = DiffHellmanCypher.calculate(args[0], args[1], args[2], args[3]);
                    System.out.println("result = " + sharedKey);
                    break;
                case 6:
                    args = InputHandler.handleShamir();
                    if (args == null) break;
                    ShamirCypher.calculate(args[0], args[1], args[2], args[3], args[4]);

                    System.out.print("Enter filename: ");
                    fileName = ConsoleInput.readString();

                    encFileName = ShamirCypher.encryptFile(fileName, args[0], args[1], args[2]);
                    System.out.println("File successfully encrypted.");
                    ShamirCypher.decryptFile(encFileName, args[0], args[3], args[4]);
                    System.out.println("File successfully decrypted.");
                    break;
                case 7:
                    args = InputHandler.handleElGamal();
                    if (args == null) break;

                    System.out.print("Enter filename: ");
                    fileName = ConsoleInput.readString();

                    encFileName = ElGamalCypher.encryptFile(args[0], args[1], fileName);
                    System.out.println("File successfully encrypted.");
                    ElGamalCypher.decryptFile(args[0], encFileName);
                    System.out.println("File successfully decrypted.");
                    break;
                case 8:
                    args = InputHandler.handleRSA();
                    if (args == null) break;

                    System.out.print("Enter filename: ");
                    fileName = ConsoleInput.readString();

                    encFileName = RSACypher.encryptFile(args[0], args[1], fileName);
                    System.out.println("File successfully encrypted.");
                    RSACypher.decryptFile(args[0], args[2], encFileName);
                    System.out.println("File successfully decrypted.");
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