package ru.sibsutis.cryptomethods.utilities;

import ru.sibsutis.cryptomethods.methods.El_GamalCypher;

import java.io.*;
import java.math.BigInteger;

public class El_GamalManager {
    public static String Encrypt(BigInteger[] args, String path) {
        int blockSize = (args[0].bitLength() - 1) / 8;
        String output = path.split("\\.")[0] + ".e";
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(output));
             FileInputStream in = new FileInputStream(path)) {

            byte[] buffer = new byte[blockSize];
            int read;

            while ((read = in.read(buffer)) != -1) {
                byte[] data = (read == blockSize) ? buffer : java.util.Arrays.copyOf(buffer, read);
                BigInteger m = new BigInteger(1, data);
                BigInteger[] enc = El_GamalCypher.Encrypt(args, m);
                byte[] aBytes = enc[0].toByteArray();
                byte[] bBytes = enc[1].toByteArray();
                out.writeInt(aBytes.length);
                out.write(aBytes);
                out.writeInt(bBytes.length);
                out.write(bBytes);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return output;
    }
    public static void Decrypt(BigInteger[] args, String path) {
        int blockSize = (args[0].bitLength() - 1) / 8;
        String output = path.split("\\.")[0] + "_decrypted" + ".txt";
        try (DataInputStream in = new DataInputStream(new FileInputStream(path));
             FileOutputStream out = new FileOutputStream(output)) {

            while (in.available() > 0) {
                int lenA = in.readInt();
                byte[] aBytes = new byte[lenA];
                in.readFully(aBytes);
                int lenB = in.readInt();
                byte[] bBytes = new byte[lenB];
                in.readFully(bBytes);

                BigInteger a = new BigInteger(aBytes);
                BigInteger b = new BigInteger(bBytes);
                BigInteger m = El_GamalCypher.Decrypt(args, new BigInteger[] {a, b});
                byte[] plain = m.toByteArray();

                if (plain.length > blockSize)
                    plain = java.util.Arrays.copyOfRange(plain, plain.length - blockSize, plain.length);
                out.write(plain);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
