package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.core.NetUser;
import ru.sibsutis.cryptomethods.core.math.FermatTest;
import ru.sibsutis.cryptomethods.core.math.PowerMod;

import java.math.BigInteger;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.core.Generator.generateRandomBigInteger;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readBigInt;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readInt;

public class DiffHellmanCypher {
    public static BigInteger[] handle() {
        System.out.println("\n=== DIFIFIFI HELLMAN ===");
        System.out.println("1. Enter numbers 'p', 'g', 'Xa', 'Xb'");
        System.out.println("2. Generate numbers 'p', 'g', 'Xa', 'Xb'");
        System.out.print("Select an option (1-2): ");

        int choice = readInt();

        BigInteger q, p, g, xA, xB;
        switch (choice) {
            case 1:
                p = readBigInt("Enter number p");
                g = readBigInt("Enter number g");
                xA = readBigInt("Enter number Xa");
                xB = readBigInt("Enter number Xb");
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

    public static BigInteger calculate(BigInteger p, BigInteger g, BigInteger xA, BigInteger xB) {
        NetUser alice = new NetUser(xA);
        NetUser bob = new NetUser(xB);
        BigInteger yA, yB;
        yA = alice.createPublicKey(g, p);
        yB = bob.createPublicKey(g, p);
        alice.createSharedKey(yB, p);
        bob.createSharedKey(yA, p);
//        System.out.println("p = " + p + " g = " + g + " Xa = " + xA + " Xb = " + xB + " Alice's open key = " + yA + " Bob's open key = " + yB);
//        System.out.println("Shared keys are: Alice = " + alice.getSharedKey() + " Bob = " + bob.getSharedKey());
//        System.out.println("As we can see, both are equal to each other. Sooo, yeah, we are cool as hell");
        return alice.getSharedKey();
    }
}
