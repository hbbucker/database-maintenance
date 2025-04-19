package br.com.hbbucker.usecase.find.index;

import br.com.hbbucker.database.DataSourceProperties;
import br.com.hbbucker.database.config.DataSourceConfigFactory;
import br.com.hbbucker.database.maintenance.DataBaseMaintenance;
import br.com.hbbucker.database.maintenance.DataBaseMaintenanceFactory;
import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexInfo;
import br.com.hbbucker.shared.database.index.IndexName;
import br.com.hbbucker.shared.database.table.SchemaName;
import br.com.hbbucker.usecase.Usecase;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class FindIndexByNameUC implements Usecase<FindIndexByNameInput, FindIndexByNameOutput>, Processor {
    private final DataBaseMaintenanceFactory dataBaseMaintenanceFactory;
    private final DataSourceConfigFactory dataSourceConfigFactory;

    @Override
    public void process(Exchange exchange) throws Exception {
        DataSourceName dataSourceName = exchange.getMessage().getHeader("x-datasource-name", DataSourceName.class);
        SchemaName schemaName = exchange.getMessage().getHeader("x-schema-name", SchemaName.class);
        IndexName indexName = exchange.getMessage().getHeader("x-index-name", IndexName.class);

        FindIndexByNameInput input = new FindIndexByNameInput(dataSourceName, schemaName, indexName);
        FindIndexByNameOutput output = execute(input);
        exchange.getMessage().setBody(output.indexInfo());
    }

    @Override
    public FindIndexByNameOutput execute(FindIndexByNameInput input) {
        DataBaseMaintenance dataBaseMaintenance = getDataBaseMaintenance(input);

        IndexInfo parameter = IndexInfo.builder()
                .schemaName(input.schemaName())
                .indexName(input.indexName())
                .build();

        Optional<IndexInfo> index = dataBaseMaintenance.findIndexByName(input.dataSourceName(), parameter);

        if (index.isEmpty()) {
            Log.errorf("Index not found %s", input.indexName());
            throw new RuntimeException(String.format("Index %s not found", input.indexName()));
        }

        return new FindIndexByNameOutput(input.dataSourceName(), index.get());
    }

    private DataBaseMaintenance getDataBaseMaintenance(FindIndexByNameInput input) {
        DataSourceProperties dataSource = dataSourceConfigFactory.get(input.dataSourceName());
        return dataBaseMaintenanceFactory.get(dataSource.getProperties().dbType());
    }

}
