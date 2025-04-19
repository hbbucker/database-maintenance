package br.com.hbbucker.usecase.recreate;

import br.com.hbbucker.database.DataSourceProperties;
import br.com.hbbucker.database.config.DataSourceConfigFactory;
import br.com.hbbucker.database.maintenance.DataBaseMaintenance;
import br.com.hbbucker.database.maintenance.DataBaseMaintenanceFactory;
import br.com.hbbucker.metrics.IndexMetrics;
import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexInfo;
import br.com.hbbucker.usecase.Usecase;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RecreateIndexUC implements Usecase<RecreateIndexInput, Void>, Processor {

    private final DataBaseMaintenanceFactory dataBaseMaintenanceFactory;
    private final DataSourceConfigFactory dataSourceConfigFactory;
    private final IndexMetrics metrics;

    @Override
    public void process(Exchange exchange) throws Exception {
        IndexInfo input = exchange.getIn().getBody(IndexInfo.class);
        DataSourceName dataSourceName = exchange.getIn().getHeader("x-datasource-name", DataSourceName.class);
        try {
            execute(new RecreateIndexInput(input, dataSourceName));
        } catch (Exception ignored) {
            // do nothing
        }
    }

    @Override
    @SneakyThrows
    public Void execute(RecreateIndexInput input) {
        metrics.markAnalyzed();

        DataBaseMaintenance dataBaseMaintenance = getDataBaseMaintenance(input);
        IndexInfo index = input.index();

        try {
            IndexInfo newIndex = dataBaseMaintenance.createIndex(input.dataSourceName(), index);
            dataBaseMaintenance.dropIndex(input.dataSourceName(), index);
            dataBaseMaintenance.renameIndex(input.dataSourceName(), newIndex.getIndexName(), index);

            metrics.markSuccess();
        } catch (Exception e) {
            metrics.markFailure();
            Log.error("Failed to rebuild index: {1}", index.getIndexName(), e);
            throw new RuntimeException("Failed to recreate index " + index.getIndexName(), e);
        }

        return null;
    }

    private DataBaseMaintenance getDataBaseMaintenance(RecreateIndexInput input) {
        DataSourceProperties dataSource = dataSourceConfigFactory.get(input.dataSourceName());
        return dataBaseMaintenanceFactory.get(dataSource.getProperties().dbType());
    }
}
