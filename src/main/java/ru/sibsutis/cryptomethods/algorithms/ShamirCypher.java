package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.algorithms.common.Cypher;
import ru.sibsutis.cryptomethods.core.math.ExtEuclid;
import ru.sibsutis.cryptomethods.core.math.FermatTest;
import ru.sibsutis.cryptomethods.core.math.PowerMod;
import ru.sibsutis.cryptomethods.core.Generator;

import java.io.*;
import java.math.BigInteger;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.core.Generator.generateRandomBigInteger;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readBigInt;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readInt;

public class ShamirCypher implements Cypher {
    private BigInteger p;
    private BigInteger cA;
    private BigInteger cB;
    private BigInteger dA;
    private BigInteger dB;

    @Override
    public void generateKeys() {
        System.out.println("\n=== SHAMIR ENCRYPTION ===");
        System.out.println("1. Enter numbers 'p', 'Ca', 'Cb''");
        System.out.println("2. Generate numbers 'P', 'Ca', 'Cb'");
        int choice = readInt("Select an option (1-2)");

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
        }
        dA = ExtEuclid.calculate(p.subtract(BigInteger.ONE), cA).getY();
        dB = ExtEuclid.calculate(p.subtract(BigInteger.ONE), cB).getY();
        dA = dA.signum() < 0? dA.add(p.subtract(BigInteger.ONE)) : dA;
        dB = dB.signum() < 0? dB.add(p.subtract(BigInteger.ONE)) : dB;
        System.out.println("p =" + p + " Ca = " + cA + " Cb = " + cB + " Da = " + dA + " Db = " + dB);
    }

    @Override
    public String encryptFile(String fileName) {
        File input = new File(BASE_PATH + fileName);
        String encFileName = "enc_" + fileName;
        File output = new File(BASE_PATH + encFileName);

        int blockSize = (p.bitLength() - 1) / 8;
        try (FileInputStream in = new FileInputStream(input);
             DataOutputStream out = new DataOutputStream(new FileOutputStream(output))) {

            byte[] buffer = new byte[blockSize];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                BigInteger block = new BigInteger(1, trimBuffer(buffer, bytesRead));

                BigInteger encryptedByAlice = PowerMod.calculate(block, cA, p);
                BigInteger encryptedByBob = PowerMod.calculate(encryptedByAlice, cB, p);

                byte[] encryptedBytes = encryptedByBob.toByteArray();

                out.writeInt(bytesRead);
                out.writeInt(encryptedBytes.length);
                out.write(encryptedBytes);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return encFileName;
    }

    @Override
    public void decryptFile(String encFileName) {
        File input = new File(BASE_PATH + encFileName);
        File output = new File(BASE_PATH + "dec_" + encFileName.substring(4));

        try (DataInputStream in = new DataInputStream(new FileInputStream(input));
             FileOutputStream out = new FileOutputStream(output)) {

            while (in.available() > 0) {
                int originalLen = in.readInt();
                int encLen = in.readInt();

                byte[] encryptedBytes = new byte[encLen];
                in.readFully(encryptedBytes);

                BigInteger block = new BigInteger(1, encryptedBytes);

                BigInteger decryptedByAlice = PowerMod.calculate(block, dA, p);
                BigInteger decryptedByBob = PowerMod.calculate(decryptedByAlice, dB, p);

                byte[] raw = decryptedByBob.toByteArray();
                byte[] restored = new byte[originalLen];
                int copyStart = Math.max(0, raw.length - originalLen);
                int copyLen = Math.min(raw.length, originalLen);

                System.arraycopy(raw, copyStart, restored, originalLen - copyLen, copyLen);
                out.write(restored);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void calculate() {
        BigInteger message = Generator.generateRandomBigInteger(new BigInteger("0"), p);
        System.out.println("Message is " + message);
        BigInteger message_1 = PowerMod.calculate(message, cA, p);    // Alisa zashifrovele
        BigInteger message_2 = PowerMod.calculate(message_1, cB, p);  // Bob zashifrovel
        BigInteger message_3 = PowerMod.calculate(message_2, dA, p);  // Alisa rasshifrovele
        BigInteger message_4 = PowerMod.calculate(message_3, dB, p);  // Bob rasshifrovele

        System.out.println("Alice's encryption: " + message_1);
        System.out.println("Bob's encryption: " + message_2);
        System.out.println("Alice's decryption: " + message_3);
        System.out.println("Bob's decryption: " + message_4);
        System.out.println("As we all can see, both first and last messages are equal to each other. This tells us that we did a great job! Congratulations!!!!");
    }

    private static byte[] trimBuffer(byte[] buffer, int length) {
        if (length < 0 || length == buffer.length) return buffer;
        byte[] trimmed = new byte[length];
        System.arraycopy(buffer, 0, trimmed, 0, length);
        return trimmed;
    }
}
