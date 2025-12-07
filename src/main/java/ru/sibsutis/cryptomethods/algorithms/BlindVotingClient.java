package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.core.math.PowerMod;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class BlindVotingClient {

    private final BigInteger N;
    private final BigInteger c;

    private BigInteger n;
    private BigInteger h;
    private BigInteger r;
    private BigInteger rInv;

    public BlindVotingClient(BigInteger N, BigInteger c) {
        this.N = N;
        this.c = c;
    }

    // ==== Клиент формирует бюллетень ====
    public BigInteger generateBallot(int vote) {
        BigInteger rnd512 = new BigInteger(512, new Random());
        BigInteger v = BigInteger.valueOf(vote);
        n = rnd512.shiftLeft(2).or(v);
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

        return h.multiply(PowerMod.calculate(r, c, N)).mod(N);
    }

    public BigInteger unblind(BigInteger sBlinded) {
        return sBlinded.multiply(rInv).mod(N);
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
