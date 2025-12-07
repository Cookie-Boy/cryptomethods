package ru.sibsutis.cryptomethods.algorithms;

import lombok.Getter;
import ru.sibsutis.cryptomethods.algorithms.common.Cypher;
import ru.sibsutis.cryptomethods.core.Generator;
import ru.sibsutis.cryptomethods.core.math.ExtEuclid;
import ru.sibsutis.cryptomethods.core.math.PowerMod;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class BlindVotingServer implements Cypher {

    @Getter
    private BigInteger d; // public

    @Getter
    private BigInteger c; // private

    @Getter
    private BigInteger N;

    private final Set<String> allowedVoters = new HashSet<>();

    private final Random rnd = new Random();

    @Override
    public void generateKeys() {
        System.out.println("=== BLIND VOTING ===");

        BigInteger p = BigInteger.probablePrime(1024, rnd);
        BigInteger q = BigInteger.probablePrime(1024, rnd);
        N = p.multiply(q);
        BigInteger f = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        do {
            c = Generator.generateRandomBigInteger(f);
        } while (!ExtEuclid.calculate(f, c).getGcd().equals(BigInteger.ONE));

        d = ExtEuclid.calculate(f, c).getY().mod(f);

        System.out.println("p = " + p);
        System.out.println("q = " + q);
        System.out.println("N = " + N);
        System.out.println("phi = " + f);
        System.out.println("d (public) = " + d);
        System.out.println("c (private) = " + c);

        System.out.println("\nThe server is now ready to accept blind signatures.\n");
    }

    @Override
    public String encryptFile(String fileName) { return ""; }

    @Override
    public void decryptFile(String encFileName) { }


    public void addAllowedVoter(String id) {
        allowedVoters.add(id);
    }

    public boolean isAllowed(String id) {
        return allowedVoters.contains(id);
    }

    public void markVoted(String id) {
        allowedVoters.remove(id);
    }

    public BigInteger signBlinded(BigInteger blindedHash, String voterId) {

        if (!isAllowed(voterId)) {
            throw new RuntimeException("User '" + voterId + "' is not allowed to vote or has already voted!");
        }

        markVoted(voterId);

        return PowerMod.calculate(blindedHash, c, N);
    }

    public boolean verify(BigInteger n, BigInteger signature) {
        BigInteger h = sha3(n.toString());
        BigInteger sCheck = PowerMod.calculate(signature, d, N);
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
