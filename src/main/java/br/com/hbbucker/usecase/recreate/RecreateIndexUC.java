package br.com.hbbucker.usecase.recreate;

import br.com.hbbucker.database.DataSourceProperties;
import br.com.hbbucker.database.config.DataSourceConfigFactory;
import br.com.hbbucker.database.maintenance.DataBaseMaintenance;
import br.com.hbbucker.database.maintenance.DataBaseMaintenanceFactory;
import br.com.hbbucker.metrics.IndexMetrics;
import br.com.hbbucker.service.CacheStatusService;
import br.com.hbbucker.shared.cache.ProcessStatus;
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
    private final CacheStatusService cacheStatusService;
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
            IndexInfo newIndex = creatingNewIndex(input, dataBaseMaintenance, index);
            dropingOldIndex(input, dataBaseMaintenance, index);
            renamingIndex(input, dataBaseMaintenance, newIndex, index);

            removeCacheStatus(input);

            metrics.markSuccess();
        } catch (Exception e) {
            metrics.markFailure();
            setErrorCacheStatus(input, e);
            Log.error("Failed to rebuild index: {1}", index.getIndexName(), e);
            throw new RuntimeException("Failed to recreate index " + index.getIndexName(), e);
        }

        return null;
    }

    private IndexInfo creatingNewIndex(RecreateIndexInput input, DataBaseMaintenance dataBaseMaintenance, IndexInfo index) {
        cacheStatusService.put(
                input.dataSourceName(),
                input.index().getTableName(),
                input.index().getIndexName(),
                new ProcessStatus("Creating new Index")
        );
        return dataBaseMaintenance.createIndex(input.dataSourceName(), index);
    }

    private void dropingOldIndex(RecreateIndexInput input, DataBaseMaintenance dataBaseMaintenance, IndexInfo index) {
        cacheStatusService.put(
                input.dataSourceName(),
                input.index().getTableName(),
                input.index().getIndexName(),
                new ProcessStatus("Dropping old Index")
        );
        dataBaseMaintenance.dropIndex(input.dataSourceName(), index);
    }

    private void renamingIndex(RecreateIndexInput input, DataBaseMaintenance dataBaseMaintenance, IndexInfo newIndex, IndexInfo index) {
        cacheStatusService.put(
                input.dataSourceName(),
                input.index().getTableName(),
                input.index().getIndexName(),
                new ProcessStatus("Rename to correct Index name")
        );
        dataBaseMaintenance.renameIndex(input.dataSourceName(), newIndex.getIndexName(), index);
    }

    private void removeCacheStatus(RecreateIndexInput input) {
        cacheStatusService.remove(
                input.dataSourceName(),
                input.index().getTableName(),
                input.index().getIndexName());
    }

    private void setErrorCacheStatus(RecreateIndexInput input, Exception e) {
        cacheStatusService.put(
                input.dataSourceName(),
                input.index().getTableName(),
                input.index().getIndexName(),
                new ProcessStatus("Failed to recreate index: " + e.getMessage())
        );
    }

    private DataBaseMaintenance getDataBaseMaintenance(RecreateIndexInput input) {
        DataSourceProperties dataSource = dataSourceConfigFactory.get(input.dataSourceName());
        return dataBaseMaintenanceFactory.get(dataSource.getProperties().dbType());
    }
}
