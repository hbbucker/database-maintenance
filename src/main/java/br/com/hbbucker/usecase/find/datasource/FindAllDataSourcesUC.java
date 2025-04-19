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
public class FindAllDataSourcesUC implements Usecase<Void, FindAllDataSourcesOutput> {
    private final DataSourceConfigList dataSourceConfigList;

    @Override
    public FindAllDataSourcesOutput execute(Void ignored) {
        FindAllDataSourcesOutput output = new FindAllDataSourcesOutput();
        List<DataSourceProperties> dataSources = dataSourceConfigList.getAll();
        dataSources.forEach(ds ->
                output.addDataSource(FindAllDataSourcesOutput.DSProperties.builder()
                        .dataSourceName(ds.getSourceName())
                        .host(ds.getProperties().host())
                        .port(ds.getProperties().port())
                        .database(ds.getProperties().database())
                        .dbType(ds.getProperties().dbType())
                        .build()));
        return output;
    }
}
