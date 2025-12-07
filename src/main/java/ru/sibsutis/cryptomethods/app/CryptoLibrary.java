package ru.sibsutis.cryptomethods.app;

import ru.sibsutis.cryptomethods.algorithms.*;
import ru.sibsutis.cryptomethods.algorithms.common.Cypher;
import ru.sibsutis.cryptomethods.core.math.*;
import ru.sibsutis.cryptomethods.io.*;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

public class CryptoLibrary {

    private static final Map<Integer, Cypher> actions = new LinkedHashMap<>();

    static {
        actions.put(6, new ShamirCypher());
        actions.put(7, new ElGamalCypher());
        actions.put(8, new RSACypher());
        actions.put(9, new VernamCypher());
        actions.put(10, new RSASignature());
        actions.put(11, new ElGamalSignature());
        actions.put(12, new GOSTSignature());
        actions.put(13, new FIPSSignature());
    }

    public static void start() {
        while (true) {
            ConsoleMenu.showMain();
            int choice = ConsoleInput.readInt("Select an option (0-15)");
            if (choice == 0) break;

            switch (choice) {
                case 1 -> runPowerMod();
                case 2 -> runFermatTest();
                case 3 -> runExtEuclid();
                case 4 -> runBabyAndGiantStep();
                case 5 -> runDiffHellmanCypher();
                case 14 -> runCryptoPoker();
                case 15 -> runBlindSignature();
                default -> runCypher(actions.get(choice));
            }
        }
    }

    private static void runPowerMod() {
        BigInteger[] args = PowerMod.handle();
        if (args == null) return;
        BigInteger a = args[0], x = args[1], p = args[2];
        BigInteger result = PowerMod.calculate(a, x, p);
        System.out.println("Result: " + a + "^" + x + " mod " + p + " = " + result);
    }

    private static void runFermatTest() {
        BigInteger[] args = FermatTest.handle();
        if (args == null) return;
        boolean isPrime = FermatTest.check(args[0], args[1]);
        System.out.println("Number " + args[0] + (isPrime ? " is prime" : " is NOT prime"));
    }

    private static void runExtEuclid() {
        BigInteger[] args = ExtEuclid.handle();
        if (args == null) return;
        EuclidResult euclidResult = ExtEuclid.calculate(args[0], args[1]);
        System.out.println("GCD(" + args[0] + ", " + args[1] + ") = " + euclidResult.getGcd());
        System.out.println("Coefficients: x = " + euclidResult.getX() + ", y = " + euclidResult.getY());

        BigInteger check = args[0].multiply(euclidResult.getX()).add(args[1].multiply(euclidResult.getY()));
        System.out.println("Checking: " + args[0] + "*" + euclidResult.getX() + " + " + args[1] + "*" + euclidResult.getY() + " = " + check);
    }

    private static void runBabyAndGiantStep() {
        BigInteger[] args = BabyAndGiantStep.handle();
        if (args == null) return;
        System.out.println(args[0] + "^x * mod " + args[1] + " = " + args[2]);
        BigInteger x = BabyAndGiantStep.calculate(args[0], args[1], args[2]);
        System.out.println("x = " + x);
    }

    private static void runDiffHellmanCypher() {
        BigInteger[] args = DiffHellmanCypher.handle();
        if (args == null) return;
        BigInteger sharedKey = DiffHellmanCypher.calculate(args[0], args[1], args[2], args[3]);
        System.out.println("result = " + sharedKey);
    }

    private static void runCypher(Cypher cypher) {
        cypher.generateKeys();
        String fileName = ConsoleInput.readString("Enter filename");
        String encFileName = cypher.encryptFile(fileName);
        cypher.decryptFile(encFileName);
    }

    private static void runCryptoPoker() {
        CryptoPokerUI.runPoker();
    }

    private static void runBlindSignature() {
        BlindVotingServer server = new BlindVotingServer();
        server.generateKeys();

        BlindVotingClient client = new BlindVotingClient(server.getN(), server.getC());

        BigInteger ballot = client.generateBallot(1);
        BigInteger hash = client.computeHash();
        BigInteger blinded = client.blind();

        BigInteger blindedSignature = server.signBlinded(blinded);
        BigInteger signature = client.unblind(blindedSignature);

        boolean ok = server.verify(ballot, signature);

        System.out.println("Signature is correct: " + ok);
    }
}