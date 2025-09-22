package ru.sibsutis.cryptomethods.utilities;

import ru.sibsutis.cryptomethods.NetUser;

import java.math.BigInteger;

public class DiffHellman {
    public static BigInteger calculate(BigInteger p, BigInteger g, BigInteger xA, BigInteger xB) {
        NetUser Alice = new NetUser(xA);
        NetUser Bob = new NetUser(xB);
        BigInteger yA, yB;
        yA = Alice.createPublicKey(g, p);
        yB = Bob.createPublicKey(g, p);
        Alice.createSharedKey(yB, p);
        Bob.createSharedKey(yA, p);
        System.out.println("p = " + p + " g = " + g + " Xa = " + xA + " Xb = " + xB + " Alice's open key = " + yA + " Bob's open key = " + yB);
        System.out.println("Shared keys are: Alice = " + Alice.getSharedKey() + " Bob = " + Bob.getSharedKey());
        System.out.println("As we can see, both are equal to each other. Sooo, yeah, we are cool as hell");
        return null;
    }
}
