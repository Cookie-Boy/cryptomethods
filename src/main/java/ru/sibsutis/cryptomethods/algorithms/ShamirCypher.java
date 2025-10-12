package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.algorithms.common.Cypher;
import ru.sibsutis.cryptomethods.core.math.PowerMod;
import ru.sibsutis.cryptomethods.core.Generator;

import java.io.*;
import java.math.BigInteger;

public class ShamirCypher implements Cypher {
    public static String encryptFile(String fileName, BigInteger p, BigInteger cA, BigInteger cB) {
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

    public static void decryptFile(String encFileName, BigInteger p, BigInteger dA, BigInteger dB) {
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

    public static void calculate(BigInteger p, BigInteger cA, BigInteger cB, BigInteger dA, BigInteger dB) {
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
