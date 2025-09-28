package ru.sibsutis.cryptomethods.methods;

import ru.sibsutis.cryptomethods.utilities.ExtendedEuclidean;
import ru.sibsutis.cryptomethods.utilities.Generator;

import java.math.BigInteger;

public class ShamirCypher {
    public static void calculate(BigInteger p, BigInteger cA, BigInteger cB, BigInteger dA, BigInteger dB) {
        BigInteger message = Generator.generateRandomBigInteger(new BigInteger("0"), p);
        System.out.println("Message is " + message);
        BigInteger message_1 = PowerMod.calculate(message, cA, p);    // Alisa zashifrovele
        BigInteger message_2 = PowerMod.calculate(message_1, cB, p);  // Bob zashifrovel
        BigInteger message_3 = PowerMod.calculate(message_2, dA, p);  // Alisa rasshifrovele
        BigInteger message_4 = PowerMod.calculate(message_3, dB, p);  // Bob rasshifrovele

        System.out.println("Alice's encryption: " + message_1);
        System.out.println("Bob's encryption: " + message_2);
        System.out.println("Alice's decryption: " + message_3);
        System.out.println("Bob's decryption: " + message_4);
        System.out.println("As we all can see, both first and last messages are equal to each other. This tells us that we did a great job! Congratulations!!!!");
    }
}
