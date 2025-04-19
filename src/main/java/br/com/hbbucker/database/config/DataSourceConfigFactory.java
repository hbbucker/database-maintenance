package br.com.hbbucker.database.config;

import br.com.hbbucker.database.DataSourceProperties;
import br.com.hbbucker.shared.database.DataSourceName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DataSourceConfigFactory {

    private final DataSourceConfigList dataSourceConfigList;

    public DataSourceProperties get(DataSourceName dataSourceName) {
        return dataSourceConfigList.get(dataSourceName);
    }
}
