package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.algorithms.common.Cypher;
import ru.sibsutis.cryptomethods.core.Generator;
import ru.sibsutis.cryptomethods.core.math.FermatTest;
import ru.sibsutis.cryptomethods.core.math.PowerMod;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readBigInt;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readInt;

public class ElGamalSignature implements Cypher {
    private BigInteger p;
    private BigInteger g;
    private BigInteger x;
    private BigInteger y;

    @Override
    public void generateKeys() {
        System.out.println("\n=== El'Gamal Signature ===");
        System.out.println("1. Enter numbers 'p', 'g', 'x'");
        System.out.println("2. Generate numbers 'p', 'g', 'x'");
        int choice = readInt("Select an option (1-2)");

        switch (choice) {
            case 1:
                System.out.print("Enter number p: ");
                p = readBigInt();
                System.out.print("Enter number g: ");
                g = readBigInt();
                do {
                    System.out.print("Enter private x: ");
                    x = readBigInt();
                } while (x.compareTo(BigInteger.ONE) < 0 || x.compareTo(p.subtract(BigInteger.ONE)) >= 0);
                break;
            case 2:
                BigInteger q;
                do {
                    q = generatePrimeNumber(50);
                    p = q.multiply(BigInteger.TWO).add(BigInteger.ONE);
                } while (!FermatTest.check(p, 50));

                for (g = BigInteger.TWO; g.compareTo(p.subtract(BigInteger.ONE)) < 0; g = g.add(BigInteger.ONE)) {
                    if (PowerMod.calculate(g, q, p).compareTo(BigInteger.ONE) != 0) {
                        break;
                    }
                }
                x = Generator.generateRandomBigInteger(BigInteger.ONE, p.subtract(BigInteger.ONE));
                System.out.println("Generated values:");
                System.out.println("p = " + p + ", g = " + g + ", x = " + x);
                break;
            default:
                System.out.println("Wrong choice.");
                return;
        }

        y = PowerMod.calculate(g, x, p); // public key
        System.out.println("Public key: p=" + p + ", g=" + g + ", y=" + y);
    }

    @Override
    public String encryptFile(String fileName) {
        int blockSize = (p.bitLength() - 1) / 8;

        File input = new File(BASE_PATH + fileName);
        String encFileName = "enc_" + fileName;
        String sigFileName = "sig_" + fileName;
        File output = new File(BASE_PATH + encFileName);

        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(output));
             FileInputStream fis = new FileInputStream(input)) {

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] buffer = new byte[blockSize];
            int read;

            // шифруем блоки (как в ElGamalCypher) и одновременно считаем хеш оригинального файла
            while ((read = fis.read(buffer)) != -1) {
                byte[] data = (read == blockSize) ? buffer : java.util.Arrays.copyOf(buffer, read);
                md.update(data);

                BigInteger m = new BigInteger(1, data);

                // выбираем случайный k для этого блока (не тот же k для подписи)
                BigInteger k = Generator.generateRandomBigInteger(BigInteger.ONE, p.subtract(BigInteger.ONE));
                while (!k.gcd(p.subtract(BigInteger.ONE)).equals(BigInteger.ONE)) {
                    k = Generator.generateRandomBigInteger(BigInteger.ONE, p.subtract(BigInteger.ONE));
                }

                BigInteger a = PowerMod.calculate(g, k, p);
                BigInteger b = PowerMod.calculate(y, k, p).multiply(m).mod(p);

                byte[] aBytes = a.toByteArray();
                byte[] bBytes = b.toByteArray();

                out.writeInt(read);
                out.writeInt(aBytes.length);
                out.write(aBytes);
                out.writeInt(bBytes.length);
                out.write(bBytes);
            }

            // формируем подпись от полного хеша (SHA-256)
            byte[] hash = md.digest();
            BigInteger h = new BigInteger(1, hash);
            // приводим х к модулю (p-1) для подписи
            BigInteger pm1 = p.subtract(BigInteger.ONE);
            BigInteger hMod = h.mod(pm1);

            // выбираем k для подписи: gcd(k, p-1) = 1
            BigInteger kSig = Generator.generateRandomBigInteger(BigInteger.ONE, pm1);
            while (!kSig.gcd(pm1).equals(BigInteger.ONE)) {
                kSig = Generator.generateRandomBigInteger(BigInteger.ONE, pm1);
            }

            BigInteger aSig = PowerMod.calculate(g, kSig, p);
            BigInteger kInv = kSig.modInverse(pm1);
            BigInteger bSig = kInv.multiply(hMod.subtract(x.multiply(aSig))).mod(pm1);
            if (bSig.signum() < 0) bSig = bSig.add(pm1);

            // записываем подпись в отдельный файл
            try (DataOutputStream sigOut = new DataOutputStream(new FileOutputStream(BASE_PATH + sigFileName))) {
                byte[] aSBytes = aSig.toByteArray();
                byte[] bSBytes = bSig.toByteArray();
                sigOut.writeInt(aSBytes.length);
                sigOut.write(aSBytes);
                sigOut.writeInt(bSBytes.length);
                sigOut.write(bSBytes);
            }

            System.out.println("Encrypted: " + encFileName);
            System.out.println("Signature saved in: " + sigFileName);
            return encFileName;

        } catch (IOException | NoSuchAlgorithmException ex) {
            throw new RuntimeException("Error during encryption/signing: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void decryptFile(String encFileName) {
        File input = new File(BASE_PATH + encFileName);
        if (!input.exists()) throw new RuntimeException("File not found: " + encFileName);

        String sigFileName = "sig_" + encFileName.replaceFirst("^enc_", "");
        File sigFile = new File(BASE_PATH + sigFileName);
        if (!sigFile.exists()) throw new RuntimeException("Signature file not found: " + sigFileName);

        File output = new File(BASE_PATH + "dec_" + encFileName.substring(4));

        try (DataInputStream in = new DataInputStream(new FileInputStream(input));
             FileOutputStream out = new FileOutputStream(output);
             DataInputStream sigIn = new DataInputStream(new FileInputStream(sigFile))) {

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Дешифрование: читаем блоки, восстанавливаем m и пишем в output, одновременно считаем хеш расшифрованного
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

                // m = b * a^{p-1-x} mod p
                BigInteger power = p.subtract(BigInteger.ONE).subtract(x);
                BigInteger aPow = PowerMod.calculate(a, power, p);
                BigInteger m = b.multiply(aPow).mod(p);

                byte[] raw = m.toByteArray();

                byte[] restored = new byte[origLen];
                int copyStart = Math.max(0, raw.length - origLen);
                int copyLen = Math.min(raw.length, origLen);
                System.arraycopy(raw, copyStart, restored, origLen - copyLen, copyLen);

                out.write(restored);
                md.update(restored);
            }

            // вычисляем хеш от расшифрованных данных
            byte[] hash = md.digest();
            BigInteger h = new BigInteger(1, hash);
            BigInteger pm1 = p.subtract(BigInteger.ONE);
            BigInteger hMod = h.mod(pm1);

            // читаем подпись (aSig, bSig) из sigFile
            int aLen = sigIn.readInt();
            byte[] aSigBytes = new byte[aLen];
            sigIn.readFully(aSigBytes);
            int bLen = sigIn.readInt();
            byte[] bSigBytes = new byte[bLen];
            sigIn.readFully(bSigBytes);

            BigInteger aSig = new BigInteger(1, aSigBytes);
            BigInteger bSig = new BigInteger(1, bSigBytes);

            // проверка: g^h ≡ y^a * a^b (mod p)
            BigInteger left = PowerMod.calculate(g, hMod, p);
            BigInteger right = PowerMod.calculate(y, aSig, p).multiply(PowerMod.calculate(aSig, bSig, p)).mod(p);

            if (left.equals(right)) {
                System.out.println("Signature is correct. Decrypted file: " + output.getName());
            } else {
                System.out.println("Signature is NOT correct! Decrypted file: " + output.getName());
            }

        } catch (IOException | NoSuchAlgorithmException ex) {
            throw new RuntimeException("Error during decrypting/verification: " + ex.getMessage(), ex);
        }
    }
}
