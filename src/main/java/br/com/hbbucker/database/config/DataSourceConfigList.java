package br.com.hbbucker.database.config;

import br.com.hbbucker.database.DataSourceProperties;
import br.com.hbbucker.shared.database.DataSourceName;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public final class DataSourceConfigList {

    private final DataSourceConfig config;
    private static final ConcurrentHashMap<DataSourceName, DataSourceProperties> CACHE = new ConcurrentHashMap<>();

    protected DataSourceProperties get(final DataSourceName dataSourceName) {
        return CACHE.computeIfAbsent(dataSourceName, key -> {
            DataSourceProperties dataSourceConfig = config.loadDatabaseConfigurations().get(dataSourceName.name());
            if (dataSourceConfig == null) {
                throw new IllegalArgumentException("No DataSource Config found for name: " + dataSourceName.name());
            }
            return dataSourceConfig;
        });
    }

    public List<DataSourceProperties> getAll() {
        return config.loadDatabaseConfigurations().getAll();
    }
}
