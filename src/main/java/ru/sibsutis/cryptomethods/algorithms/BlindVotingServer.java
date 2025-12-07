package ru.sibsutis.cryptomethods.algorithms;

import lombok.Getter;
import ru.sibsutis.cryptomethods.algorithms.common.Cypher;
import ru.sibsutis.cryptomethods.core.Generator;
import ru.sibsutis.cryptomethods.core.math.ExtEuclid;
import ru.sibsutis.cryptomethods.core.math.PowerMod;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class BlindVotingServer implements Cypher {

    private BigInteger d;

    @Getter
    private BigInteger N;
    @Getter
    private BigInteger c;

    private final Random rnd = new Random();

    @Override
    public void generateKeys() {
        System.out.println("=== CЛЕПОЕ ГОЛОСОВАНИЕ: ГЕНЕРАЦИЯ RSA ===");

        BigInteger p = BigInteger.probablePrime(1024, rnd);
        BigInteger q = BigInteger.probablePrime(1024, rnd);
        N = p.multiply(q);
        BigInteger f = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        do {
            d = Generator.generateRandomBigInteger(f);
        } while(ExtEuclid.calculate(f, d).getGcd().compareTo(BigInteger.ONE) != 0);
        c = ExtEuclid.calculate(f, d).getY().mod(f);

        System.out.println("p = " + p);
        System.out.println("q = " + q);
        System.out.println("N = " + N);
        System.out.println("phi = " + f);
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

    public BigInteger signBlinded(BigInteger blindedHash) {
        return PowerMod.calculate(blindedHash, d, N);
    }

    public boolean verify(BigInteger n, BigInteger signature) {
        BigInteger h = sha3(n.toString());
        BigInteger sCheck = PowerMod.calculate(signature, c, N);
        return sCheck.equals(h);
    }

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
