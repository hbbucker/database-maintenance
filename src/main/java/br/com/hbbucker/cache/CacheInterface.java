package br.com.hbbucker.cache;

import java.time.Duration;
import java.util.Set;

public interface CacheInterface<T> {
    void put(String key, T value);
    void put(String key, T value, Duration ttl);
    void remove(String key);
    T get(String key);
    Set<String> getKeys();
}
