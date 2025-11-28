package ru.sibsutis.cryptomethods.algorithms;

import ru.sibsutis.cryptomethods.core.Gambler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.min;
import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readInt;
import static ru.sibsutis.cryptomethods.io.ConsoleInput.readOptionalBigInt;



public class CryptoPoker {
    BigInteger p;
    int n;
    int cards = 52;
    List<BigInteger> deck = new ArrayList<>();
    List<Gambler> list = new ArrayList<>();

    public void generateKeys() {

        System.out.println("\n=== Crypto poker ===");
        System.out.println("Enter numbers 'p' (space to autogenerate) and 'n' 2 ≤ n ≤ 52'");
        System.out.print("Enter number p: ");
        p = readOptionalBigInt();
        if(p.equals(BigInteger.ZERO)) {
            p = generatePrimeNumber(50);
            System.out.println("p = " + p);
        }
        System.out.print("Enter number n: ");

        do {
            n = readInt();
        } while(n < 2 || n > cards);

        for(int i = 2; i < cards + 2; i++)
            deck.add(BigInteger.valueOf(i));

        for(int i = 0; i < n; i++)
            list.add(new Gambler(p));
    }

    public void simulate() {
        for(int i = 0; i < n; i++) {
            deck = list.get(i).encHand(deck, p); // следующий шифрует колоду
            Collections.shuffle(deck); // тасовка
        }

        int handSize = n == cards? 1: cards / (n + 1); // Поровну делим карты между игроками и столом + остаток ( если 52 игрока, на столе 0 карт)
        List<BigInteger> hand = new ArrayList<>();

        for(int i = 0; i < n; i++) {            // каждому даем по handSize карт, проходя в цикле от одного к другому
            Gambler gambler = list.get(i);
            for(int j = 0; j < handSize; j++)
                hand.add(deck.removeLast());
            gambler.takeHand(hand);
            hand.clear();
            System.out.println("Player " + (i + 1) + ": ");
            gambler.show();
        }                                       // остаток карт пойдет на стол

        for (int i = 0; i < n; i++) {
            hand = list.get(i).passHand();
            for (int j = 1; j < n + 1; j++) {
                hand = list.get((i + j) % n).deEncHand(hand, p); // игрок отдает карты другому, они по кругу дешифруют, и владелец колоды дешифрует последним
            }
            Gambler gambler = list.get(i);
            gambler.takeHand(hand);
            System.out.println("Player " + (i + 1) + ": ");
            gambler.show();
        }

        if(!deck.isEmpty()) {
            System.out.println("\n" + "The board:");
                for (int j = 0; j < n ; j++) {
                    deck = list.get(j).deEncHand(deck, p);
                }

                for(int i = 0; i < deck.size(); i++)
                    System.out.println("        " + i + " - " + deck.get(i));
        }

    }

}
