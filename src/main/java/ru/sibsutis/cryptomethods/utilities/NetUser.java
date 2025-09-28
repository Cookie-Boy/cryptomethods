package ru.sibsutis.cryptomethods.utilities;

import ru.sibsutis.cryptomethods.methods.PowerMod;

import java.math.BigInteger;

public class NetUser {
    private BigInteger secret;
    private BigInteger sharedKey;

    public NetUser() {
        this.secret = Generator.generateRandomBigInteger();
    }

    public NetUser(BigInteger secret) {
        this.secret = secret;
    }

    public BigInteger createPublicKey(BigInteger a, BigInteger p) {
        return PowerMod.calculate(a, secret, p);
    }

    public void createSharedKey(BigInteger publicKey, BigInteger p) {
        this.sharedKey = PowerMod.calculate(publicKey, secret, p);
    }

    public BigInteger getSharedKey() {
        return sharedKey;
    }
}
