package br.com.hbbucker.service;

import br.com.hbbucker.cache.ProcessStatusCache;
import br.com.hbbucker.shared.cache.ProcessStatus;
import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexName;
import br.com.hbbucker.shared.database.table.TableName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public final class CacheStatusService {
    public static final String CACHE_KEY = "ds:status:%s:%s:%s";
    private final ProcessStatusCache cache;

    public void put(
            final DataSourceName dataSourceName,
            final TableName tableName,
            final IndexName indexName,
            final ProcessStatus status) {

        cache.put(getKey(dataSourceName, tableName, indexName), status);
    }

    public void put(
            final DataSourceName dataSourceName,
            final TableName tableName,
            final IndexName indexName,
            final ProcessStatus status,
            final Duration ttl) {

        cache.put(getKey(dataSourceName, tableName, indexName), status, ttl);
    }

    public ProcessStatus get(
            final DataSourceName dataSourceName,
            final TableName tableName,
            final IndexName indexName) {

        return cache.get(getKey(dataSourceName, tableName, indexName));
    }

    public void remove(
            final DataSourceName dataSourceName,
            final TableName tableName,
            final IndexName indexName) {
        cache.remove(getKey(dataSourceName, tableName, indexName));
    }

    public Set<String> getKeys() {
        return cache.getKeys();
    }

    private String getKey(
            final DataSourceName dataSourceName,
            final TableName tableName,
            final IndexName indexName) {
        return CACHE_KEY.formatted(dataSourceName.name(), tableName.name(), indexName.name());
    }

}
