package ru.sibsutis.cryptomethods;

import ru.sibsutis.cryptomethods.utilities.Generator;
import ru.sibsutis.cryptomethods.utilities.PowerMod;

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
        sharedKey = PowerMod.calculate(publicKey, secret, p);
    }

    public BigInteger getSharedKey() {
        return sharedKey;
    }
}
