package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.algorithms.common.Cypher;
import ru.sibsutis.cryptomethods.core.Generator;
import ru.sibsutis.cryptomethods.core.math.FermatTest;
import ru.sibsutis.cryptomethods.core.math.PowerMod;

import java.io.*;
import java.math.BigInteger;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.core.Generator.generateRandomBigInteger;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readBigInt;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readInt;

public class VernamCypher implements Cypher {

    byte[] key;
    @Override
    public void generateKeys() {
        System.out.println("\n=== Vernam Cypher ===");
        BigInteger[] args = DiffHellmanCypher.handle();
        key = DiffHellmanCypher.calculate(args[0], args[1], args[2], args[3]).toByteArray();
    }

    private void Xorize(byte[] data) {
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)(data[i] ^ key[i]);
    }

    @Override
    public String encryptFile(String fileName) {
        File input = new File(BASE_PATH + fileName);
        String encFileName = "enc_" + fileName;
        File output = new File(BASE_PATH + encFileName);

        try (FileInputStream in = new FileInputStream(input);
             FileOutputStream out = new FileOutputStream(output)) {

            byte[] buffer = new byte[key.length];
            int read;

            while ((read = in.read(buffer)) != -1) {
                byte[] trimmed = trimBuffer(buffer, read);

                Xorize(trimmed);
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

            byte[] buffer = new byte[key.length];
            int read;

            while ((read = in.read(buffer)) != -1) {
                byte[] trimmed = trimBuffer(buffer, read);

                Xorize(trimmed);
                out.write(trimmed);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error while decrypting file: " + ex.getMessage(), ex);
        }
    }

    private static byte[] trimBuffer(byte[] buffer, int length) {
        if (length < 0 || length == buffer.length) return buffer;
        byte[] trimmed = new byte[length];
        System.arraycopy(buffer, 0, trimmed, 0, length);
        return trimmed;
    }
}