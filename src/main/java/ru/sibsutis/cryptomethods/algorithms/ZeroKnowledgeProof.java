package ru.sibsutis.cryptomethods.algorithms;

import lombok.AllArgsConstructor;
import ru.sibsutis.cryptomethods.core.Edge;
import ru.sibsutis.cryptomethods.core.Generator;
import ru.sibsutis.cryptomethods.core.math.EuclidResult;
import ru.sibsutis.cryptomethods.core.math.ExtEuclid;
import ru.sibsutis.cryptomethods.core.math.PowerMod;

import java.math.BigInteger;
import java.util.*;

import static ru.sibsutis.cryptomethods.core.Generator.generatePrimeNumber;

public class ZeroKnowledgeProof {
    private static final Random rand = new Random();
    private int[] originalColors;
    private List<Edge> edges;
    private int n;

    public ZeroKnowledgeProof(int n, List<Edge> edges, int[] colors) {
        this.n = n;
        this.edges = edges;
        this.originalColors = colors.clone();
    }

    public int[] generateColorPermutation() {
        List<Integer> colors = new ArrayList<>(Arrays.asList(0, 1, 2));
        Collections.shuffle(colors, rand);
        return colors.stream().mapToInt(i -> i).toArray();
    }

    public int[] applyPermutation(int[] permutation) {
        int[] permutedColors = new int[n];
        for (int i = 0; i < n; i++) {
            permutedColors[i] = permutation[originalColors[i]];
        }
        return permutedColors;
    }
    @AllArgsConstructor
    private static class IterationData {
        BigInteger N;
        BigInteger c;
        BigInteger d;
        BigInteger Z;
    }

    public void calculate() {
        for(int i = 0; i < 5 * edges.size(); i++) {
            int[] newColors = applyPermutation(generateColorPermutation());
            BigInteger[] r = new BigInteger[newColors.length];
            List<IterationData> data = new ArrayList<>();

            for(int j = 0; j < r.length; j++) {
                r[j] = Generator.generateRandomBigInteger().shiftLeft(2).or(BigInteger.valueOf(newColors[j]));
                BigInteger q = generatePrimeNumber(50);
                BigInteger p = generatePrimeNumber(50);
                BigInteger N = p.multiply(q);
                BigInteger f = N.subtract(p).subtract(q).add(BigInteger.ONE);
                BigInteger c;
                EuclidResult res;
                do {
                    c = Generator.generateRandomBigInteger(f);
                    res = ExtEuclid.calculate(f, c);
                } while(res.getGcd().compareTo(BigInteger.ONE) != 0);
                BigInteger d = ExtEuclid.calculate(f, c).getY().mod(f);

                BigInteger Z = PowerMod.calculate(r[j], c, N);

                data.add(new IterationData(N, c, d, Z));
            }

            Edge randEdge = edges.get(rand.nextInt(0, edges.size()));

            int v = randEdge.getV();
            int u = randEdge.getU();

            BigInteger Zv, Zu;

            Zv = PowerMod.calculate(data.get(v).Z, data.get(v).d, data.get(v).N).and(BigInteger.valueOf(3));
            Zu = PowerMod.calculate(data.get(u).Z, data.get(u).d, data.get(u).N).and(BigInteger.valueOf(3));

            if(Zv.equals(Zu)) {
                System.out.println("Alice is lying!!");
                System.out.println(randEdge + "have same-colored vertices: " + v + " " + u + " : " + originalColors[v] + " and " + originalColors[u]);
                return;
            }

        }

        System.out.println("Alice is not lying with probability of e^-5");
    }
}
