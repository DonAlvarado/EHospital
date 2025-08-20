package estructuras;

import java.util.Random;

public class hashFunction {
    private long a, b, p, m;

    // Constructor para inicializar con números aleatorios dentro de los rangos
    public hashFunction(long p, long m) {
        this.p = p;
        this.m = m;
        Random rand = new Random();
        this.a = rand.nextInt((int) (p - 1)) + 1;
        this.b = rand.nextInt((int) p);
    }

    public long hashLong(long key) {
        long hash = (a * (key + b)) % p;
        if (hash < 0) {
            hash += p;
        }
        return hash % m;
    }

    public long hashString(String key) {
        long value = 0;
        int base = 31;

        for (int i = 0; i < key.length(); i++) {
            char character = key.charAt(i);
            value = (value * base + character) % p;
        }

        long hash = ((long) a * value + b) % p;
        return (long) (hash % m);
    }
}
