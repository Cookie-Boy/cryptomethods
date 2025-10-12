package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.algorithms.common.Cypher;

import java.io.*;
import java.math.BigInteger;

public class RSACypher implements Cypher {
    /**
     * Заглушка для RSA-шифрования одного блока данных.
     * @param message исходное сообщение (блок)
     * @param e открытая экспонента
     * @param n модуль RSA
     * @return зашифрованное сообщение
     */
    private static BigInteger encrypt(BigInteger message, BigInteger e, BigInteger n) {
        // TODO: Реализовать RSA-шифрование: c = m^e mod n
        return message; // заглушка
    }

    /**
     * Заглушка для RSA-дешифрования одного блока данных.
     * @param cipher зашифрованное сообщение (блок)
     * @param d закрытая экспонента
     * @param n модуль RSA
     * @return расшифрованное сообщение
     */
    private static BigInteger decrypt(BigInteger cipher, BigInteger d, BigInteger n) {
        // TODO: Реализовать RSA-дешифрование: m = c^d mod n
        return cipher; // заглушка
    }

    /**
     * Шифрование файла алгоритмом RSA.
     * @param n модуль RSA
     * @param e открытая экспонента
     * @param fileName имя исходного файла (должен находиться в resources)
     * @return имя зашифрованного файла
     */
    public static String encryptFile(BigInteger n, BigInteger e, String fileName) {
        int blockSize = (n.bitLength() - 1) / 8;

        File input = new File(BASE_PATH + fileName);
        String encFileName = "enc_" + fileName;
        File output = new File(BASE_PATH + encFileName);

        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(output));
             FileInputStream in = new FileInputStream(input)) {

            byte[] buffer = new byte[blockSize];
            int read;

            while ((read = in.read(buffer)) != -1) {
                byte[] data = (read == blockSize) ? buffer : java.util.Arrays.copyOf(buffer, read);
                BigInteger m = new BigInteger(1, data);

                BigInteger c = encrypt(m, e, n); // заглушка RSA-шифрования

                byte[] cBytes = c.toByteArray();

                out.writeInt(read); // исходная длина блока
                out.writeInt(cBytes.length);
                out.write(cBytes);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Ошибка при шифровании файла: " + ex.getMessage(), ex);
        }

        return encFileName;
    }

    /**
     * Дешифрование файла, зашифрованного RSA.
     * @param n модуль RSA
     * @param d закрытая экспонента
     * @param encFileName имя зашифрованного файла
     */
    public static void decryptFile(BigInteger n, BigInteger d, String encFileName) {
        File input = new File(BASE_PATH + encFileName);
        File output = new File(BASE_PATH + "dec_" + encFileName.substring(4));

        try (DataInputStream in = new DataInputStream(new FileInputStream(input));
             FileOutputStream out = new FileOutputStream(output)) {

            while (in.available() > 0) {
                int origLen = in.readInt();

                int lenC = in.readInt();
                byte[] cBytes = new byte[lenC];
                in.readFully(cBytes);

                BigInteger c = new BigInteger(1, cBytes);

                BigInteger m = decrypt(c, d, n); // заглушка RSA-дешифрования
                byte[] raw = m.toByteArray();

                // выравнивание по правому краю, чтобы восстановить ведущие нули
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
}
