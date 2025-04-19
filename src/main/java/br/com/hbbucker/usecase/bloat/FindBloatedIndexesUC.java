package br.com.hbbucker.usecase.bloat;

import br.com.hbbucker.database.DataSourceProperties;
import br.com.hbbucker.database.config.DataSourceConfigFactory;
import br.com.hbbucker.database.maintenance.DataBaseMaintenance;
import br.com.hbbucker.database.maintenance.DataBaseMaintenanceFactory;
import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexInfo;
import br.com.hbbucker.usecase.Usecase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class FindBloatedIndexesUC implements Usecase<FindBloatedIndexesInput, FindBloatedIndexesOutput>, Processor {
    private final DataBaseMaintenanceFactory dataBaseMaintenanceFactory;
    private final DataSourceConfigFactory dataSourceConfigFactory;

    @Override
    public void process(Exchange exchange) throws Exception {
        DataSourceName dataSourceName = exchange.getMessage().getHeader("x-datasource-name", DataSourceName.class);
        FindBloatedIndexesOutput output = execute(new FindBloatedIndexesInput(dataSourceName));
        exchange.getMessage().setBody(output.indexInfos());
    }

    @Override
    public FindBloatedIndexesOutput execute(FindBloatedIndexesInput indexesInput) {
        DataBaseMaintenance dataBaseMaintenance = getDataBaseMaintenance(indexesInput);
        List<IndexInfo> result = dataBaseMaintenance.findBloatedIndexes(indexesInput.dataSourceName());

        return new FindBloatedIndexesOutput(result);
    }

    private DataBaseMaintenance getDataBaseMaintenance(FindBloatedIndexesInput indexesInput) {
        DataSourceProperties dataSource = dataSourceConfigFactory.get(indexesInput.dataSourceName());
        return dataBaseMaintenanceFactory.get(dataSource.getProperties().dbType());
    }

}
