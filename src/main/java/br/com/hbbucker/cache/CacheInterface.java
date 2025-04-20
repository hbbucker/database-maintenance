package br.com.hbbucker.cache;

import java.util.Set;

public interface CacheInterface<T> {
    void put(String key, T value);
    void remove(String key);
    T get(String key);
    Set<String> getKeys();
}
