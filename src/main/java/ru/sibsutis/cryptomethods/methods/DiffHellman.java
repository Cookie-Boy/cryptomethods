package ru.sibsutis.cryptomethods.methods;

import ru.sibsutis.cryptomethods.utilities.NetUser;

import java.math.BigInteger;

public class DiffHellman {
    public static BigInteger calculate(BigInteger p, BigInteger g, BigInteger xA, BigInteger xB) {
        NetUser alice = new NetUser(xA);
        NetUser bob = new NetUser(xB);
        BigInteger yA, yB;
        yA = alice.createPublicKey(g, p);
        yB = bob.createPublicKey(g, p);
        alice.createSharedKey(yB, p);
        bob.createSharedKey(yA, p);
        System.out.println("p = " + p + " g = " + g + " Xa = " + xA + " Xb = " + xB + " Alice's open key = " + yA + " Bob's open key = " + yB);
        System.out.println("Shared keys are: Alice = " + alice.getSharedKey() + " Bob = " + bob.getSharedKey());
        System.out.println("As we can see, both are equal to each other. Sooo, yeah, we are cool as hell");
        return alice.getSharedKey();
    }
}
