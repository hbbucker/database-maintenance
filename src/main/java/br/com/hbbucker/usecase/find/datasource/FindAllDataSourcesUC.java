package br.com.hbbucker.usecase.find.datasource;

import br.com.hbbucker.database.DataSourceProperties;
import br.com.hbbucker.database.config.DataSourceConfigList;
import br.com.hbbucker.usecase.Usecase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public final class FindAllDataSourcesUC implements Usecase<Void, FindAllDataSourcesOutput> {
    private final DataSourceConfigList dataSourceConfigList;

    @Override
    public FindAllDataSourcesOutput execute(final Void ignored) {
        List<DataSourceProperties> dataSources = dataSourceConfigList.getAll();
        return buildOutput(dataSources);
    }

    private FindAllDataSourcesOutput buildOutput(final List<DataSourceProperties> dataSources) {
        FindAllDataSourcesOutput output = new FindAllDataSourcesOutput();
        dataSources.forEach(ds -> output.addDataSource(mapToDSProperties(ds)));
        return output;
    }

    private FindAllDataSourcesOutput.DSProperties mapToDSProperties(final DataSourceProperties ds) {
        return FindAllDataSourcesOutput.DSProperties.builder()
                .dataSourceName(ds.getSourceName())
                .host(ds.getProperties().host())
                .port(ds.getProperties().port())
                .database(ds.getProperties().database())
                .dbType(ds.getProperties().dbType())
                .build();
    }
}
