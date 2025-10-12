package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.algorithms.common.Cypher;
import ru.sibsutis.cryptomethods.core.math.PowerMod;

import java.io.*;
import java.math.BigInteger;

public class RSACypher implements Cypher {
    private static BigInteger encrypt(BigInteger message, BigInteger d, BigInteger N) {
        return PowerMod.calculate(message, d, N);
    }

    private static BigInteger decrypt(BigInteger cryptogram, BigInteger c, BigInteger N) {
        return PowerMod.calculate(cryptogram, c, N);
    }

    public static String encryptFile(BigInteger N, BigInteger d, String fileName) {
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

    public static void decryptFile(BigInteger N, BigInteger c, String encFileName) {
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
