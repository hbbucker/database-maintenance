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
public final class FindBloatedIndexesUC implements Usecase<FindBloatedIndexesInput, FindBloatedIndexesOutput>, Processor {
    private final DataBaseMaintenanceFactory dataBaseMaintenanceFactory;
    private final DataSourceConfigFactory dataSourceConfigFactory;

    @Override
    public void process(final Exchange exchange) throws Exception {
        DataSourceName dataSourceName = exchange.getMessage().getHeader("x-datasource-name", DataSourceName.class);
        FindBloatedIndexesOutput output = execute(new FindBloatedIndexesInput(dataSourceName));
        exchange.getMessage().setBody(output.indexInfos());
    }

    @Override
    public FindBloatedIndexesOutput execute(final FindBloatedIndexesInput indexesInput) {
        DataBaseMaintenance dataBaseMaintenance = getDataBaseMaintenance(indexesInput.dataSourceName());
        List<IndexInfo> bloatedIndexes = findBloatedIndexes(indexesInput.dataSourceName(), dataBaseMaintenance);
        return new FindBloatedIndexesOutput(bloatedIndexes);
    }

    private List<IndexInfo> findBloatedIndexes(final DataSourceName dataSourceName, final DataBaseMaintenance dataBaseMaintenance) {
        return dataBaseMaintenance.findBloatedIndexes(dataSourceName);
    }

    private DataBaseMaintenance getDataBaseMaintenance(final DataSourceName dataSourceName) {
        DataSourceProperties dataSource = dataSourceConfigFactory.get(dataSourceName);
        return dataBaseMaintenanceFactory.getMaintenanceByType(dataSource.getProperties().dbType());
    }

}
