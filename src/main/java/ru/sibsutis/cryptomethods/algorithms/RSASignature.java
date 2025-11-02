package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.algorithms.common.Cypher;
import ru.sibsutis.cryptomethods.core.Generator;
import ru.sibsutis.cryptomethods.core.math.ExtEuclid;
import ru.sibsutis.cryptomethods.core.math.FermatTest;
import ru.sibsutis.cryptomethods.core.math.PowerMod;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readBigInt;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readInt;

public class RSASignature implements Cypher {
    private BigInteger N, d; // public
    private BigInteger c; // private

    @Override
    public void generateKeys() {
        System.out.println("\n=== RSA Signature ===");
        System.out.println("1. Enter numbers 'p', 'q'");
        System.out.println("2. Generate numbers 'p', 'q'");
        int choice = readInt("Select an option (1-2)");

        BigInteger q, p, f;
        switch (choice) {
            case 1:
                do {
                    System.out.print("Enter number p: ");
                    p = readBigInt();
                } while (!FermatTest.check(p, 100));
                do {
                    System.out.print("Enter number q: ");
                    q = readBigInt();
                } while (!FermatTest.check(q, 100));
                System.out.println("You entered: p = " + p + " q = " + q);
                break;
            case 2:
                q = generatePrimeNumber(50);
                p = generatePrimeNumber(50);
                System.out.println("p = " + p + ", q = " + q);
                break;
            default:
                System.out.println("Wrong choice.");
                return;
        }

        N = p.multiply(q);
        f = N.subtract(p).subtract(q).add(BigInteger.ONE);
        do {
            d = Generator.generateRandomBigInteger(f);
        } while (ExtEuclid.calculate(f, d).getGcd().compareTo(BigInteger.ONE) != 0);
        c = ExtEuclid.calculate(f, d).getY().mod(f);
//        if (c.signum() < 0) c = c.add(f);

        System.out.println("Generated/public parameters: N=" + N + ", d=" + d);
    }

    private BigInteger encrypt(BigInteger message, BigInteger d, BigInteger N) {
        return PowerMod.calculate(message, d, N);
    }

    private BigInteger decrypt(BigInteger cryptogram, BigInteger c, BigInteger N) {
        return PowerMod.calculate(cryptogram, c, N);
    }

    @Override
    public String encryptFile(String fileName) {
        int blockSize = (N.bitLength() - 1) / 8;

        File input = new File(BASE_PATH + fileName);
        String encFileName = "enc_" + fileName;
        String signatureFileName = "sig_" + fileName;
        File output = new File(BASE_PATH + encFileName);

        try (FileInputStream in = new FileInputStream(input);
             DataOutputStream out = new DataOutputStream(new FileOutputStream(output))) {

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] buffer = new byte[blockSize];
            int read;

            while ((read = in.read(buffer)) != -1) {
                byte[] block = trimBuffer(buffer, read);
                md.update(block);

                BigInteger m = new BigInteger(1, block);

                BigInteger c = encrypt(m, d, N);

                byte[] cBytes = c.toByteArray();

                out.writeInt(read);
                out.writeInt(cBytes.length);
                out.write(cBytes);
            }

            byte[] hash = md.digest();
            BigInteger h = new BigInteger(1, hash);
            if (h.compareTo(N) >= 0) h = h.mod(N);

            BigInteger s = PowerMod.calculate(h, c, N);
            byte[] sigBytes = s.toByteArray();

            try (FileOutputStream sigOut = new FileOutputStream(BASE_PATH + signatureFileName)) {
                sigOut.write(sigBytes);
            }

            System.out.println("Signature saved in: " + signatureFileName);
            return encFileName;

        } catch (IOException | NoSuchAlgorithmException ex) {
            throw new RuntimeException("Error during encrypting and signing: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void decryptFile(String encFileName) {
        File input = new File(BASE_PATH + encFileName);
        if (!input.exists()) throw new RuntimeException("File not found: " + encFileName);

        String sigFileName = "sig_" + encFileName.replace("enc_", "");
        File sigFile = new File(BASE_PATH + sigFileName);
        if (!sigFile.exists()) throw new RuntimeException("Signature file not found: " + sigFileName);

        File output = new File(BASE_PATH + "dec_" + encFileName.substring(4));

        try (DataInputStream in = new DataInputStream(new FileInputStream(input));
             FileOutputStream out = new FileOutputStream(output)) {

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            while (in.available() > 0) {
                int origLen = in.readInt();

                int lenC = in.readInt();
                byte[] cBytes = new byte[lenC];
                in.readFully(cBytes);

                BigInteger cryptogram = new BigInteger(1, cBytes);

                BigInteger m = decrypt(cryptogram, c, N);
                byte[] raw = m.toByteArray();

                byte[] restored = new byte[origLen];
                int copyStart = Math.max(0, raw.length - origLen);
                int copyLen = Math.min(raw.length, origLen);
                System.arraycopy(raw, copyStart, restored, origLen - copyLen, copyLen);

                out.write(restored);
                md.update(restored); // хешируем восстановленные данные
            }

            byte[] hash = md.digest();
            BigInteger h = new BigInteger(1, hash);
            if (h.compareTo(N) >= 0) h = h.mod(N);

            try (FileInputStream sigIn = new FileInputStream(sigFile)) {
                byte[] sigBytes = sigIn.readAllBytes();

                BigInteger s = new BigInteger(1, sigBytes);
                BigInteger hCheck = PowerMod.calculate(s, d, N);

                if (hCheck.equals(h)) {
                    System.out.println("Sign is correct!");
                } else {
                    System.out.println("Sign IS NOT correct!");
                }
            }

        } catch (IOException | NoSuchAlgorithmException ex) {
            throw new RuntimeException("Error during decrypting and signature checking: " + ex.getMessage(), ex);
        }
    }

    private static byte[] trimBuffer(byte[] buffer, int length) {
        if (length < 0 || length == buffer.length) return buffer;
        byte[] trimmed = new byte[length];
        System.arraycopy(buffer, 0, trimmed, 0, length);
        return trimmed;
    }
}
