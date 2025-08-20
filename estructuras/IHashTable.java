package estructuras;

public interface IHashTable<K, V> {
    void put(K key, V value);
    V get(K key);
    boolean remove(K key);

    // 🔹 Alias opcionales para compatibilidad
    default void insert(K key, V value) {
        put(key, value);
    }

    default V search(K key) {
        return get(key);
    }
}
