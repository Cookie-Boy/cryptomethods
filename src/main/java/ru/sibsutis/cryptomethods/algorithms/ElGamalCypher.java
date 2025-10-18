package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.algorithms.common.Cypher;
import ru.sibsutis.cryptomethods.core.math.FermatTest;
import ru.sibsutis.cryptomethods.core.math.PowerMod;
import ru.sibsutis.cryptomethods.core.Generator;
import ru.sibsutis.cryptomethods.core.NetUser;

import java.io.*;
import java.math.BigInteger;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.core.Generator.generateRandomBigInteger;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readBigInt;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readInt;

public class ElGamalCypher implements Cypher {
    private static NetUser alice;
    private static NetUser bob;

    private BigInteger p, g, xA, xB;

    @Override
    public void generateKeys() {
        System.out.println("\n=== El'Gamal ===");
        System.out.println("1. Enter numbers 'p', 'g', 'Xa', 'Xb'");
        System.out.println("2. Generate numbers 'p', 'g', 'Xa', 'Xb'");
        int choice = readInt("Select an option (1-2)");

        BigInteger q;
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
        }
    }

    public BigInteger[] encrypt(BigInteger p, BigInteger g, BigInteger k, BigInteger bY, BigInteger message) {
        BigInteger[] pair = new BigInteger[2];
        pair[0] = PowerMod.calculate(g, k, p);
        pair[1] = PowerMod.calculate(bY, k, p).multiply(message).mod(p);
        return pair;
    }

    public BigInteger decrypt(BigInteger p, BigInteger power, BigInteger[] pair) {
        return PowerMod.calculate(pair[0], power, p).multiply(pair[1]).mod(p);
    }

    @Override
    public String encryptFile(String fileName) {
        alice = new NetUser(Generator.generateRandomBigInteger(BigInteger.ZERO, p));
        bob = new NetUser(Generator.generateRandomBigInteger(BigInteger.ZERO, p));
        int blockSize = (p.bitLength() - 1) / 8;

        File input = new File(BASE_PATH + fileName);
        String encFileName = "enc_" + fileName;
        File output = new File(BASE_PATH + encFileName);

        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(output));
             FileInputStream in = new FileInputStream(input)) {

            byte[] buffer = new byte[blockSize];
            int read;

            while ((read = in.read(buffer)) != -1) {
                byte[] data = (read == blockSize) ? buffer : java.util.Arrays.copyOf(buffer, read);
                BigInteger m = new BigInteger(1, data); // беззнаковое

                BigInteger[] enc = encrypt(p, g, alice.getSecret(), bob.createPublicKey(g, p), m);
                alice.setSecret(Generator.generateRandomBigInteger(p, BigInteger.valueOf(Long.MAX_VALUE)));

                byte[] aBytes = enc[0].toByteArray();
                byte[] bBytes = enc[1].toByteArray();

                out.writeInt(read);
                out.writeInt(aBytes.length);
                out.write(aBytes);
                out.writeInt(bBytes.length);
                out.write(bBytes);
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
                int origLen = in.readInt();

                int lenA = in.readInt();
                byte[] aBytes = new byte[lenA];
                in.readFully(aBytes);

                int lenB = in.readInt();
                byte[] bBytes = new byte[lenB];
                in.readFully(bBytes);

                BigInteger a = new BigInteger(1, aBytes);
                BigInteger b = new BigInteger(1, bBytes);

                BigInteger m = decrypt(p, p.subtract(BigInteger.ONE).subtract(bob.getSecret()), new BigInteger[]{a, b});
                byte[] raw = m.toByteArray();

                byte[] restored = new byte[origLen];
                int copyStart = Math.max(0, raw.length - origLen);
                int copyLen = Math.min(raw.length, origLen);
                System.arraycopy(raw, copyStart, restored, origLen - copyLen, copyLen);

                out.write(restored);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
