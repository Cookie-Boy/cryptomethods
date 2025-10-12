package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.algorithms.common.Cypher;
import ru.sibsutis.cryptomethods.core.math.PowerMod;
import ru.sibsutis.cryptomethods.core.Generator;
import ru.sibsutis.cryptomethods.core.NetUser;

import java.io.*;
import java.math.BigInteger;

public class ElGamalCypher implements Cypher {
    private static NetUser alice;
    private static NetUser bob;

    public static BigInteger[] encrypt(BigInteger p, BigInteger g, BigInteger k, BigInteger bY, BigInteger message) {
        BigInteger[] pair = new BigInteger[2];
        pair[0] = PowerMod.calculate(g, k, p);
        pair[1] = PowerMod.calculate(bY, k, p).multiply(message).mod(p);
        return pair;
    }

    public static BigInteger decrypt(BigInteger p, BigInteger power, BigInteger[] pair) {
        return PowerMod.calculate(pair[0], power, p).multiply(pair[1]).mod(p);
    }

    public static String encryptFile(BigInteger p, BigInteger g, String fileName) {
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

    public static void decryptFile(BigInteger p, String encFileName) {
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

                // выравниваем байты по правому краю, чтобы восстановить ведущие нули
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
