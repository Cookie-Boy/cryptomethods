package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.algorithms.common.Cypher;
import ru.sibsutis.cryptomethods.core.math.EuclidResult;
import ru.sibsutis.cryptomethods.core.math.ExtEuclid;
import ru.sibsutis.cryptomethods.core.math.FermatTest;
import ru.sibsutis.cryptomethods.core.math.PowerMod;
import ru.sibsutis.cryptomethods.core.Generator;
import ru.sibsutis.cryptomethods.core.NetUser;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.core.Generator.generateRandomBigInteger;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readBigInt;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readInt;

public class ElGamalSignature implements Cypher {
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
        String signatureFileName = "sig_" + fileName;
        File output = new File(BASE_PATH + encFileName);

        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(output));
             FileInputStream in = new FileInputStream(input)) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[blockSize];
            int read;

            while ((read = in.read(buffer)) != -1) {
                byte[] data = (read == blockSize) ? buffer : java.util.Arrays.copyOf(buffer, read);
                BigInteger m = new BigInteger(1, data); // беззнаковое
                md.update(data);

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
            byte[] hash = md.digest();
            BigInteger h = new BigInteger(1, hash).mod(p.subtract(BigInteger.ONE));
            BigInteger kSig;
            EuclidResult res;
            BigInteger p_1 = p.subtract(BigInteger.ONE);
            do {
                kSig = Generator.generateRandomBigInteger(BigInteger.ONE, p_1);
                res = ExtEuclid.calculate(p_1, kSig);
            } while (!res.getGcd().equals(BigInteger.ONE));
            BigInteger r = PowerMod.calculate(g, kSig, p);
            BigInteger u = h.subtract(r.multiply(alice.getSecret())).mod(p_1);
            BigInteger s = res.getY().multiply(u).mod(p_1);

            byte[] rb = r.toByteArray();
            byte[] sb = s.toByteArray();

            System.out.println("r = " + r + " s = " + s);

            try (DataOutputStream sigOut = new DataOutputStream(new FileOutputStream(BASE_PATH + signatureFileName))) {
                sigOut.writeInt(rb.length);
                sigOut.write(rb);
                sigOut.writeInt(sb.length);
                sigOut.write(sb);
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return encFileName;
    }

    @Override
    public void decryptFile(String encFileName) {
        File input = new File(BASE_PATH + encFileName);
        File output = new File(BASE_PATH + "dec_" + encFileName.substring(4));
        String sigFileName = "sig_" + encFileName.replace("enc_", "");
        File sigFile = new File(BASE_PATH + sigFileName);

        try (DataInputStream in = new DataInputStream(new FileInputStream(input));
             FileOutputStream out = new FileOutputStream(output)) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
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
                md.update(restored);
            }

            byte[] hash = md.digest();
            BigInteger h = new BigInteger(1, hash).mod(p.subtract(BigInteger.ONE));
            BigInteger r, s;

            try (DataInputStream sigIn = new DataInputStream(new FileInputStream(sigFile))) {
                int rb = sigIn.readInt();
                r = new BigInteger(1, sigIn.readNBytes(rb));
                int sb = sigIn.readInt();
                s = new BigInteger(1, sigIn.readNBytes(sb));
            }

            BigInteger Yr = PowerMod.calculate(alice.createPublicKey(g, p), r, p);
            BigInteger Rs = PowerMod.calculate(r, s, p);
            BigInteger Ghp = PowerMod.calculate(g, h, p);
            BigInteger leftPart = Yr.multiply(Rs).mod(p);

            if(Ghp.compareTo(leftPart) == 0) {
                System.out.println("Sign is correct!");
            } else {
                System.out.println("Sign IS NOT correct!");
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
