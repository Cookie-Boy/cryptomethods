package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.algorithms.common.Cypher;
import ru.sibsutis.cryptomethods.core.math.FermatTest;
import ru.sibsutis.cryptomethods.core.math.PowerMod;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.core.Generator.generateRandomBigInteger;

public class VernamCypher implements Cypher {

    private List<byte[]> keys = new ArrayList<>();

    @Override
    public void generateKeys() {
        System.out.println("\n=== Vernam Cypher ===");
//        BigInteger[] args = DiffHellmanCypher.handle();
//        key = DiffHellmanCypher.calculate(args[0], args[1], args[2], args[3]).toByteArray();
    }

    private void generateSharedKey() {
        BigInteger q, p, g, xA, xB;
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
        BigInteger key = DiffHellmanCypher.calculate(p, g, xA, xB);
        keys.add(key.toByteArray());
    }

    private void xorize(byte[] data, byte[] key) {
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)(data[i] ^ key[i]);
    }

    @Override
    public String encryptFile(String fileName) {
        File input = new File(BASE_PATH + fileName);
        String encFileName = "enc_" + fileName;
        File output = new File(BASE_PATH + encFileName);

        keys.clear();

        try (FileInputStream in = new FileInputStream(input);
             FileOutputStream out = new FileOutputStream(output)) {

            int read = 0;
            while (read != -1) {
                generateSharedKey();
                byte[] key = keys.getLast();

                byte[] buffer = new byte[key.length];
                read = in.read(buffer);
                byte[] trimmed = trimBuffer(buffer, read);
                xorize(trimmed, key);
                out.write(trimmed);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error while encrypting file: " + ex.getMessage(), ex);
        }

        return encFileName;
    }

    @Override
    public void decryptFile(String encFileName) {
        File input = new File(BASE_PATH + encFileName);
        File output = new File(BASE_PATH + "dec_" + encFileName.substring(4));

        try (FileInputStream in = new FileInputStream(input);
             FileOutputStream out = new FileOutputStream(output)) {

            for (byte[] key : keys) {
                byte[] buffer = new byte[key.length];
                int read = in.read(buffer);
                if (read == -1) {
                    System.out.println("oooops");
                    return;
                }

                byte[] trimmed = trimBuffer(buffer, read);
                xorize(trimmed, key);
                out.write(trimmed);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error while decrypting file: " + ex.getMessage(), ex);
        }

        keys.clear();
    }

    private static byte[] trimBuffer(byte[] buffer, int length) {
        if (length < 0 || length == buffer.length) return buffer;
        byte[] trimmed = new byte[length];
        System.arraycopy(buffer, 0, trimmed, 0, length);
        return trimmed;
    }
}