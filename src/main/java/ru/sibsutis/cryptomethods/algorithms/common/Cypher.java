package ru.sibsutis.cryptomethods.algorithms.common;

public interface Cypher {
    String BASE_PATH = "src/main/resources/";
    void generateKeys();
    String encryptFile(String fileName);
    void decryptFile(String encFileName);
}
