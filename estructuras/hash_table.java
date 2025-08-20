package estructuras;

import java.util.*;

public class hash_table<K, V> implements IHashTable<K, V> {

    // Entrada genérica
    private static class Entry<K, V> {
        K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final long capacity;
    private final List<Entry<K, V>>[] table;
    private final hashFunction hashFunction;

    // Constructor: capacity es tamaño de la tabla, primeP es el primo para hash universal
    @SuppressWarnings("unchecked")
    public hash_table(int capacity, long primeP) {
        this.capacity = capacity;
        this.table = new List[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
        }
        this.hashFunction = new hashFunction(primeP, capacity);
    }

    // ================== PUT ==================
    @Override
    public void put(K key, V value) {
        int idx = hashIndex(key);
        for (Entry<K, V> e : table[idx]) {
            if (e.key.equals(key)) {
                e.value = value;
                return;
            }
        }
        table[idx].add(new Entry<>(key, value));
    }

    // ================== GET ==================
    @Override
    public V get(K key) {
        int idx = hashIndex(key);
        for (Entry<K, V> e : table[idx]) {
            if (e.key.equals(key)) return e.value;
        }
        return null;
    }

    // ================== REMOVE ==================
    @Override
    public boolean remove(K key) {
        int idx = hashIndex(key);
        Iterator<Entry<K, V>> it = table[idx].iterator();
        while (it.hasNext()) {
            Entry<K, V> e = it.next();
            if (e.key.equals(key)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    // ================== FUNCION HASH ==================
    private int hashIndex(K key) {
        long hash;
        if (key instanceof String) {
            hash = hashFunction.hashString((String) key);
        } else if (key instanceof Integer) {
            hash = hashFunction.hashLong((Integer) key);
        } else if (key instanceof Long) {
            hash = hashFunction.hashLong((Long) key);
        } else {
            throw new IllegalArgumentException("Unsupported key type: " + key.getClass());
        }
        return (int) (hash % capacity);
    }

    // ================== KEYS ==================
    public Iterable<K> keys() {
        List<K> lista = new ArrayList<>();
        for (List<Entry<K, V>> bucket : table) {
            for (Entry<K, V> e : bucket) {
                lista.add(e.key);
            }
        }
        return lista;
    }

    // ================== ENTRIES ==================
    public Iterable<Map.Entry<K, V>> entries() {
        List<Map.Entry<K, V>> lista = new ArrayList<>();
        for (List<Entry<K, V>> bucket : table) {
            for (Entry<K, V> e : bucket) {
                lista.add(new AbstractMap.SimpleEntry<>(e.key, e.value));
            }
        }
        return lista;
    }
}