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

    private BigInteger p;
    private BigInteger g;
    private BigInteger a;       // секретный ключ пользователя A
    private BigInteger A;       // открытый ключ A = g^a mod p
    private BigInteger B;       // открытый ключ другой стороны (вводится)
    private BigInteger K;       // общий секретный ключ

    @Override
    public void generateKeys() {
        System.out.println("\n=== Vernam Cypher ===");
        System.out.println("1. Enter 'p', 'g', and 'B' manually");
        System.out.println("2. Generate 'p', 'g' and 'B' automatically");
        int choice = readInt("Select an option (1-2)");

        switch (choice) {
            case 1:
                do {
                    p = readBigInt("Enter prime number p");
                } while (!FermatTest.check(p, 100));

                g = readBigInt("Enter primitive root g");
                B = readBigInt("Enter public key of the other side (B)");
                break;

            case 2:
                p = generatePrimeNumber(100);
                g = generateRandomBigInteger(p.subtract(BigInteger.ONE));
                B = generateRandomBigInteger();
                System.out.println("Generated p = " + p + ", g = " + g + ", B = " + B);
                break;

            default:
                System.out.println("Invalid option.");
                return;
        }

        a = Generator.generateRandomBigInteger(p.subtract(BigInteger.ONE));
        A = PowerMod.calculate(g, a, p);
        System.out.println("Your public key A = " + A);

        if (B != null && !B.equals(BigInteger.ZERO)) {
            // 🔥 Here K is generated using Diffie-Hellman:
            K = PowerMod.calculate(B, a, p);
            System.out.println("Shared secret key K = " + K);
        } else {
            System.out.println("Shared key is not computed yet (waiting for B).");
        }
    }

    private byte[] encrypt(byte[] data, byte[] key) {
        // TODO: implement XOR operation
        return data;
    }

    private byte[] decrypt(byte[] data, byte[] key) {
        // TODO: implement XOR operation
        return data;
    }

    @Override
    public String encryptFile(String fileName) {
        if (K == null) {
            throw new IllegalStateException("Key K not generated. Run generateKeys() first.");
        }

        File input = new File(BASE_PATH + fileName);
        String encFileName = "enc_" + fileName;
        File output = new File(BASE_PATH + encFileName);

        try (FileInputStream in = new FileInputStream(input);
             FileOutputStream out = new FileOutputStream(output)) {

            byte[] buffer = new byte[4096];
            byte[] keyBytes = K.toByteArray();
            int read;

            while ((read = in.read(buffer)) != -1) {
                byte[] trimmed = trimBuffer(buffer, read);

                byte[] encrypted = encrypt(trimmed, keyBytes);
                out.write(encrypted);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error while encrypting file: " + ex.getMessage(), ex);
        }

        return encFileName;
    }

    @Override
    public void decryptFile(String encFileName) {
        if (K == null) {
            throw new IllegalStateException("Key K not generated. Run generateKeys() first.");
        }

        File input = new File(BASE_PATH + encFileName);
        File output = new File(BASE_PATH + "dec_" + encFileName.substring(4));

        try (FileInputStream in = new FileInputStream(input);
             FileOutputStream out = new FileOutputStream(output)) {

            byte[] buffer = new byte[4096];
            byte[] keyBytes = K.toByteArray();
            int read;

            while ((read = in.read(buffer)) != -1) {
                byte[] trimmed = trimBuffer(buffer, read);

                byte[] decrypted = decrypt(trimmed, keyBytes);
                out.write(decrypted);
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