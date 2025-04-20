package br.com.hbbucker.service;

import br.com.hbbucker.cache.ProcessStatusCache;
import br.com.hbbucker.shared.cache.ProcessStatus;
import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexName;
import br.com.hbbucker.shared.database.table.TableName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CacheStatusService {
    public static final String CACHE_KEY = "ds:status:%s:%s:%s";
    private final ProcessStatusCache cache;

    public void put(
            DataSourceName dataSourceName,
            TableName tableName,
            IndexName indexName,
            ProcessStatus status) {

        cache.put(getKey(dataSourceName, tableName, indexName), status);
    }

    public ProcessStatus get(
            DataSourceName dataSourceName,
            TableName tableName,
            IndexName indexName) {

        return cache.get(getKey(dataSourceName, tableName, indexName));
    }

    public void remove(DataSourceName dataSourceName,
                       TableName tableName,
                       IndexName indexName) {
        cache.remove(getKey(dataSourceName, tableName, indexName));
    }

    public Set<String> getKeys() {
        return cache.getKeys();
    }

    private String getKey(DataSourceName dataSourceName, TableName tableName, IndexName indexName) {
        return CACHE_KEY.formatted(dataSourceName.name(), tableName.name(), indexName.name());
    }

}
