package br.com.hbbucker.database.config;

import br.com.hbbucker.database.DataSourceProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RegisterForReflection
public final class DataSourcePropertiesList {
    private final ConcurrentHashMap<String, DataSourceProperties> map = new ConcurrentHashMap<>();

    public DataSourceProperties get(final String sourceName) {
        return map.computeIfAbsent(sourceName, key -> DataSourceProperties.builder().build());
    }

    public List<DataSourceProperties> getAll() {
        return List.copyOf(map.values());
    }
}
