package ru.sibsutis.cryptomethods.io;

import java.math.BigInteger;
import java.util.Scanner;

public class ConsoleInput {
    private static final Scanner scanner = new Scanner(System.in);

    public static BigInteger readBigInt() {
        while (true) {
            try {
                System.out.print("Enter number: ");
                String input = scanner.nextLine();
                return new BigInteger(input);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid integer");
            }
        }
    }

    public static int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter an integer: ");
            scanner.next();
        }
        int result = scanner.nextInt();
        scanner.nextLine();
        return result;
    }

    public static String readString() {
        while (!scanner.hasNext()) {
            System.out.print("Please enter an string: ");
            scanner.next();
        }
        String result = scanner.next();
        scanner.nextLine();
        return result;
    }
}
