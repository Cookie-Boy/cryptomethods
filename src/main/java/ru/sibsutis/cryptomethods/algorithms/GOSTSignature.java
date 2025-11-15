package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.algorithms.common.Cypher;
import ru.sibsutis.cryptomethods.core.math.FermatTest;
import ru.sibsutis.cryptomethods.core.math.PowerMod;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static ru.sibsutis.cryptomethods.core.Generator.*;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readBigInt;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readInt;

public class GOSTSignature implements Cypher {

    private BigInteger p, q, a;
    private BigInteger x;
    private BigInteger y;

    @Override
    public void generateKeys() {
        System.out.println("\n=== GOST Signature ===");
        System.out.println("1. Enter numbers 'p', 'q', 'a', 'x'");
        System.out.println("2. Generate numbers 'p', 'q', 'a', 'x'");
        int choice = readInt("Select an option (1-2)");

        switch (choice) {
            case 1:
                System.out.print("Enter prime number p (31 bit): ");
                p = readBigInt();
                System.out.print("Enter prime number q (16 bit): ");
                q = readBigInt();
                System.out.print("Enter number a: ");
                a = readBigInt();
                System.out.print("Enter secret key x (0 < x < q): ");
                x = readBigInt();

                BigInteger pMinusOne = p.subtract(BigInteger.ONE);
                if (!pMinusOne.mod(q).equals(BigInteger.ZERO)) {
                    System.out.println("Error: p-1 must be divisible by q");
                    return;
                }

                if (!PowerMod.calculate(a, q, p).equals(BigInteger.ONE)) {
                    System.out.println("Error: a^q mod p must equal 1");
                    return;
                }

                y = PowerMod.calculate(a, x, p);
                break;

            case 2:
                generateGOSTParameters();
                break;

            default:
                System.out.println("Wrong choice.");
                return;
        }

        System.out.println("Generated parameters:");
        System.out.println("p = " + p + " (" + p.bitLength() + " bits)");
        System.out.println("q = " + q + " (" + q.bitLength() + " bits)");
        System.out.println("a = " + a);
        System.out.println("x = " + x + " (secret key)");
        System.out.println("y = " + y + " (public key)");
    }

    private void generateGOSTParameters() {
        q = generatePrimeWithBitLength(16);

        BigInteger b;
        do {
            BigInteger minB = BigInteger.valueOf(32768);
            BigInteger maxB = BigInteger.valueOf(65536);
            b = generateRandomBigInteger(minB, maxB);
            p = b.multiply(q).add(BigInteger.ONE);
        } while (!FermatTest.check(p, 100) || p.bitLength() != 31);

        BigInteger g;
        do {
            g = generateRandomBigInteger(BigInteger.TWO, p.subtract(BigInteger.ONE));
            a = PowerMod.calculate(g, b, p);
        } while (a.equals(BigInteger.ONE));

        x = generateRandomBigInteger(BigInteger.ONE, q.subtract(BigInteger.ONE));
        y = PowerMod.calculate(a, x, p);
    }

    public BigInteger[] generateSignature(byte[] message) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(message);
            BigInteger h = new BigInteger(1, hashBytes).mod(q);
            if (h.equals(BigInteger.ZERO)) {
                h = BigInteger.ONE;
            }

            BigInteger r, s = BigInteger.ONE, k;

            do {
                k = generateRandomBigInteger(BigInteger.ONE, q.subtract(BigInteger.ONE));
                r = PowerMod.calculate(a, k, p).mod(q);
                if (r.equals(BigInteger.ZERO)) {
                    continue;
                }

                s = k.multiply(h).add(x.multiply(r)).mod(q);

            } while (s.equals(BigInteger.ZERO));

            return new BigInteger[]{r, s};

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public boolean verifySignature(byte[] message, BigInteger r, BigInteger s) {
        if (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(q) >= 0 ||
                s.compareTo(BigInteger.ZERO) <= 0 || s.compareTo(q) >= 0) {
            return false;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(message);
            BigInteger h = new BigInteger(1, hashBytes).mod(q);
            if (h.equals(BigInteger.ZERO)) {
                h = BigInteger.ONE;
            }

            BigInteger hInv = h.modInverse(q);

            BigInteger u1 = s.multiply(hInv).mod(q);
            BigInteger u2 = r.negate().multiply(hInv).mod(q);

            BigInteger term1 = PowerMod.calculate(a, u1, p);
            BigInteger term2 = PowerMod.calculate(y, u2, p);
            BigInteger v = term1.multiply(term2).mod(p).mod(q);

            return v.equals(r);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    @Override
    public String encryptFile(String fileName) {
        File input = new File(BASE_PATH + fileName);
        String signatureFileName = "sig_" + fileName;

        try (FileInputStream in = new FileInputStream(input);
             DataOutputStream sigOut = new DataOutputStream(new FileOutputStream(BASE_PATH + signatureFileName))) {

            byte[] fileContent = in.readAllBytes();

            BigInteger[] signature = generateSignature(fileContent);
            BigInteger r = signature[0];
            BigInteger s = signature[1];

            System.out.println("Generated signature:");
            System.out.println("r = " + r);
            System.out.println("s = " + s);

            byte[] rBytes = r.toByteArray();
            byte[] sBytes = s.toByteArray();

            sigOut.writeInt(rBytes.length);
            sigOut.write(rBytes);
            sigOut.writeInt(sBytes.length);
            sigOut.write(sBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileName;
    }

    @Override
    public void decryptFile(String fileName) {
        String signatureFileName = "sig_" + fileName;
        File input = new File(BASE_PATH + fileName);
        File sigFile = new File(BASE_PATH + signatureFileName);

        try (FileInputStream in = new FileInputStream(input);
             DataInputStream sigIn = new DataInputStream(new FileInputStream(sigFile))) {

            byte[] fileContent = in.readAllBytes();

            int rLen = sigIn.readInt();
            BigInteger r = new BigInteger(sigIn.readNBytes(rLen));

            int sLen = sigIn.readInt();
            BigInteger s = new BigInteger(sigIn.readNBytes(sLen));

            boolean isValid = verifySignature(fileContent, r, s);

            if (isValid) {
                System.out.println("GOST signature is VALID");
            } else {
                System.out.println("GOST signature is INVALID");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}