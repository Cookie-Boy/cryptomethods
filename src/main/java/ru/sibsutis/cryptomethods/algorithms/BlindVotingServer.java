package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.algorithms.common.Cypher;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class BlindVotingServer implements Cypher {

    private BigInteger p, q;
    private BigInteger N;     // Модуль
    private BigInteger phi;   // Функция Эйлера
    private BigInteger d;     // Закрытый ключ
    private BigInteger c;     // Открытый ключ

    private final Random rnd = new Random();

    // ==== Генерация ключей (точка входа вместо AnonymousVotingApp) ====
    @Override
    public void generateKeys() {
        System.out.println("=== БЛИНД-ГОЛОСОВАНИЕ: ГЕНЕРАЦИЯ RSA ===");

        p = BigInteger.probablePrime(512, rnd);
        q = BigInteger.probablePrime(512, rnd);
        N = p.multiply(q);
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        c = BigInteger.valueOf(65537);
        d = c.modInverse(phi);

        System.out.println("p = " + p);
        System.out.println("q = " + q);
        System.out.println("N = " + N);
        System.out.println("phi = " + phi);
        System.out.println("c (public) = " + c);
        System.out.println("d (private) = " + d);

        System.out.println("\nТеперь сервер готов принимать слепые подписи.\n");
    }

    @Override
    public String encryptFile(String fileName) {
        return "";
    }

    @Override
    public void decryptFile(String encFileName) {

    }

    // ==== Подписать ослеплённый хэш ====
    public BigInteger signBlinded(BigInteger blindedHash) {
        return blindedHash.modPow(c, N);
    }

    // ==== Проверить итоговый бюллетень ====
    public boolean verify(BigInteger n, BigInteger signature) {
        BigInteger h = sha3(n.toString());
        BigInteger sCheck = signature.modPow(d, N);
        return sCheck.equals(h);
    }

    public BigInteger getN() { return N; }
    public BigInteger getC() { return c; }

    private BigInteger sha3(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            byte[] hash = digest.digest(s.getBytes());
            return new BigInteger(1, hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
