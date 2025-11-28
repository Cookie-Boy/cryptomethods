package ru.sibsutis.cryptomethods.core;

import ru.sibsutis.cryptomethods.core.math.EuclidResult;
import ru.sibsutis.cryptomethods.core.math.ExtEuclid;
import ru.sibsutis.cryptomethods.core.math.PowerMod;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;

public class Gambler {
    private BigInteger secret;
    private BigInteger antisecret;
    private List<BigInteger> hand;

    public Gambler(BigInteger p) {
        EuclidResult res;
        do {
            secret = generatePrimeNumber(100, BigInteger.valueOf(2), p.subtract(BigInteger.TWO));
            res = ExtEuclid.calculate(p.subtract(BigInteger.ONE), secret);
        } while(!res.getGcd().equals(BigInteger.ONE));
        this.antisecret = res.getY();
        this.antisecret = antisecret.signum() < 0? antisecret.add(p.subtract(BigInteger.ONE)) : antisecret;
    }

    public void takeHand(List<BigInteger> hand) {
        this.hand = new ArrayList<>(hand);
    }

    public List<BigInteger> passHand() {
        return hand;
    }

    public List<BigInteger> encHand(List<BigInteger> hand, BigInteger p) {
        List<BigInteger> cards = new java.util.ArrayList<>();
        for(BigInteger card: hand)
            cards.add(PowerMod.calculate(card, secret, p));
        return cards;
    }

    public List<BigInteger> deEncHand(List<BigInteger> hand, BigInteger p) {
        List<BigInteger> cards = new java.util.ArrayList<>();
        for(BigInteger card: hand)
            cards.add(PowerMod.calculate(card, antisecret, p));
        return cards;
    }

    public void show() {
        for(int i = 0; i < hand.size(); i++)
            System.out.println("        " + (i + 1) + " - " + hand.get(i));
    }
}
