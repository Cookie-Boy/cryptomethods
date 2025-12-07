package ru.sibsutis.cryptomethods.algorithms;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class BlindVotingClient {

    private final BigInteger N;
    private final BigInteger c;

    private BigInteger n;       // Бюллетень
    private BigInteger h;       // Хэш
    private BigInteger r;       // Ослепляющий множитель
    private BigInteger rInv;    // Обратный r

    public BlindVotingClient(BigInteger N, BigInteger c) {
        this.N = N;
        this.c = c;
    }

    // ==== Клиент формирует бюллетень ====
    public BigInteger generateBallot(int vote) {
        BigInteger rnd512 = new BigInteger(512, new Random());
        BigInteger v512 = BigInteger.valueOf(vote).shiftLeft(510);

        n = rnd512.or(v512);
        return n;
    }

    // ==== Хэширование n ====
    public BigInteger computeHash() {
        h = sha3(n.toString());
        return h;
    }

    // ==== Ослепление ====
    public BigInteger blind() {
        Random random = new Random();
        do {
            r = new BigInteger(N.bitLength() - 1, random);
        } while (!r.gcd(N).equals(BigInteger.ONE));

        rInv = r.modInverse(N);

        return h.multiply(r.modPow(c, N)).mod(N);
    }

    // ==== Расслепление ====
    public BigInteger unblind(BigInteger sBlinded) {
        return sBlinded.multiply(rInv).mod(N);
    }

    public BigInteger getN() { return n; }
    public BigInteger getHash() { return h; }

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
