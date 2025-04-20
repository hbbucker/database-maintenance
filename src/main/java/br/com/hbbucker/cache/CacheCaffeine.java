package br.com.hbbucker.cache;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CaffeineCache;
import io.quarkus.logging.Log;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class CacheCaffeine<T> implements CacheInterface<T> {
    private final Cache cache;

    @Override
    public void put(String key, T value) {
        CompletableFuture<T> valueFuture = CompletableFuture.completedFuture(value);
        cache.as(CaffeineCache.class).put(key, valueFuture);

    }

    @Override
    public void remove(String key) {
        cache.as(CaffeineCache.class).invalidate(key)
                .subscribe().with(
                        result -> Log.info("Cache Removed " + result),
                        failure -> Log.error("Cache remove failure" + failure)
                );
    }

    @Override
    public T get(String key) {
        try {
            return (T) cache.as(CaffeineCache.class)
                    .getIfPresent(key)
                    .get();


        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> getKeys() {
        return cache.as(CaffeineCache.class).keySet()
                .stream()
                .map(Objects::toString)
                .collect(Collectors.toSet());
    }
}
