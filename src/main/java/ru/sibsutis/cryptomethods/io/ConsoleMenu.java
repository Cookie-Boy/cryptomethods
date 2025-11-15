package ru.sibsutis.cryptomethods.io;

public class ConsoleMenu {
    public static void showMain() {
        System.out.println("\n=== CRYPTOGRAPHIC LIBRARY (512-bit) ===");
        System.out.println("1. Fast exponentiation by modulo");
        System.out.println("2. Fermat's Primality Test");
        System.out.println("3. Generalized Euclidean Algorithm");
        System.out.println("4. Baby's Step, Giant's Step");
        System.out.println("5. Diff Hellman");
        System.out.println("6. Shamir's cypher");
        System.out.println("7. El'Gamal's cypher");
        System.out.println("8. RSA cypher");
        System.out.println("9. Vernam cypher");
        System.out.println("10. RSA signature");
        System.out.println("11. El'Gamal's signature");
        System.out.println("12. GOST signature");
        System.out.println("0. Exit");
    }
}
