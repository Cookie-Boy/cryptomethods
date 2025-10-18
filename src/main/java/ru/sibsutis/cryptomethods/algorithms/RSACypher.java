package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.algorithms.common.Cypher;
import ru.sibsutis.cryptomethods.core.Generator;
import ru.sibsutis.cryptomethods.core.math.ExtEuclid;
import ru.sibsutis.cryptomethods.core.math.FermatTest;
import ru.sibsutis.cryptomethods.core.math.PowerMod;

import java.io.*;
import java.math.BigInteger;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readBigInt;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readInt;

public class RSACypher implements Cypher {
    private BigInteger N, d, c;

    @Override
    public void generateKeys() {
        System.out.println("\n=== RSA Cypher ===");
        System.out.println("1. Enter numbers 'p', 'q'");
        System.out.println("2. Generate numbers 'p', 'q'");
        int choice = readInt("Select an option (1-2)");

        BigInteger q, p, f;
        switch (choice) {
            case 1:
                do {
                    System.out.print("Enter number p: ");
                    p = readBigInt();
                } while(!FermatTest.check(p, 100));
                do {
                    System.out.print("Enter number q: ");
                    q = readBigInt();
                } while(!FermatTest.check(q, 100));
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
        } while(ExtEuclid.calculate(f, d).getGcd().compareTo(BigInteger.ONE) != 0);
        c = ExtEuclid.calculate(f, d).getY().mod(f);
//        c = c.signum() < 0? c.add(f.subtract(BigInteger.ONE)) : c;
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
        File output = new File(BASE_PATH + encFileName);

        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(output));
             FileInputStream in = new FileInputStream(input)) {

            byte[] buffer = new byte[blockSize];
            int read;

            while ((read = in.read(buffer)) != -1) {
                BigInteger m = new BigInteger(1, trimBuffer(buffer, read));

                BigInteger c = encrypt(m, d, N);

                byte[] cBytes = c.toByteArray();

                out.writeInt(read);
                out.writeInt(cBytes.length);
                out.write(cBytes);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Ошибка при шифровании файла: " + ex.getMessage(), ex);
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
            }
        } catch (IOException ex) {
            throw new RuntimeException("Ошибка при дешифровании файла: " + ex.getMessage(), ex);
        }
    }

    private static byte[] trimBuffer(byte[] buffer, int length) {
        if (length < 0 || length == buffer.length) return buffer;
        byte[] trimmed = new byte[length];
        System.arraycopy(buffer, 0, trimmed, 0, length);
        return trimmed;
    }
}
