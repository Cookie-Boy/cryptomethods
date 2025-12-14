package ru.sibsutis.cryptomethods.app;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

import ru.sibsutis.cryptomethods.algorithms.ZeroKnowledgeProof;
import ru.sibsutis.cryptomethods.io.GraphReader;

import static ru.sibsutis.cryptomethods.io.ConsoleInput.readString;

public class RgrEfimov {
    public static void main(String [] args) throws IOException {
        GraphReader reader = new GraphReader();
        String fileName = readString("Enter file name (Write 'auto' to autogenerate)");
        if(Objects.equals(fileName, "auto")) {
            fileName = GraphReader.generateAndSaveGraph();
        }

        try {
            reader.readFromFile(fileName);
            System.out.println("M = " + reader.getM() + " N = " + reader.getN());
            System.out.println("Edges: " + reader.getEdges());
            System.out.println("Vertices: " + Arrays.toString(reader.getColors()));
        } catch (IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }

        ZeroKnowledgeProof proof = new ZeroKnowledgeProof(reader.getN(), reader.getEdges(), reader.getColors());

        proof.calculate();
    }
}
