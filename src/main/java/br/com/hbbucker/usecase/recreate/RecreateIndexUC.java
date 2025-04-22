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
public final class RecreateIndexUC implements Usecase<RecreateIndexInput, Void>, Processor {

    private final DataBaseMaintenanceFactory dataBaseMaintenanceFactory;
    private final DataSourceConfigFactory dataSourceConfigFactory;
    private final CacheStatusService cacheStatusService;
    private final IndexMetrics metrics;

    @Override
    public void process(final Exchange exchange) throws Exception {
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
    public Void execute(final RecreateIndexInput input) {
        metrics.markAnalyzed();
        DataBaseMaintenance dataBaseMaintenance = getDataBaseMaintenance(input);

        try {
            recreateIndex(input, dataBaseMaintenance);
            removeCacheStatus(input);
            metrics.markSuccess();
        } catch (Exception e) {
            handleRecreationFailure(input, e);
        }

        return null;
    }

    private void recreateIndex(final RecreateIndexInput input, final DataBaseMaintenance dataBaseMaintenance) {
        IndexInfo index = input.index();
        IndexInfo newIndex = createNewIndex(input, dataBaseMaintenance, index);
        dropOldIndex(input, dataBaseMaintenance, index);
        renameIndex(input, dataBaseMaintenance, newIndex, index);
    }

    private void handleRecreationFailure(final RecreateIndexInput input, final Exception e) {
        metrics.markFailure();
        setErrorCacheStatus(input, e);
        Log.error("Failed to rebuild index: {}", input.index().getIndexName(), e);
        throw new RuntimeException("Failed to recreate index " + input.index().getIndexName(), e);
    }

    private IndexInfo createNewIndex(
            final RecreateIndexInput input,
            final DataBaseMaintenance dataBaseMaintenance,
            final IndexInfo index) {
        updateCacheStatus(input, "Creating new Index");
        return dataBaseMaintenance.createNewIndex(input.dataSourceName(), index);
    }

    private void dropOldIndex(final RecreateIndexInput input, final DataBaseMaintenance dataBaseMaintenance, final IndexInfo index) {
        updateCacheStatus(input, "Dropping old Index");
        dataBaseMaintenance.deleteIndex(input.dataSourceName(), index);
    }

    private void renameIndex(
            final RecreateIndexInput input,
            final DataBaseMaintenance dataBaseMaintenance,
            final IndexInfo newIndex,
            final IndexInfo index) {
        updateCacheStatus(input, "Rename to correct Index name");
        dataBaseMaintenance.updateIndexName(input.dataSourceName(), newIndex.getIndexName(), index);
    }

    private void updateCacheStatus(
            final RecreateIndexInput input,
            final String statusMessage) {
        cacheStatusService.put(
                input.dataSourceName(),
                input.index().getTableName(),
                input.index().getIndexName(),
                new ProcessStatus(statusMessage)
        );
    }

    private void removeCacheStatus(final RecreateIndexInput input) {
        cacheStatusService.remove(
                input.dataSourceName(),
                input.index().getTableName(),
                input.index().getIndexName());
    }

    private void setErrorCacheStatus(final RecreateIndexInput input, final Exception e) {
        cacheStatusService.put(
                input.dataSourceName(),
                input.index().getTableName(),
                input.index().getIndexName(),
                new ProcessStatus("Failed to recreate index: " + e.getMessage())
        );
    }

    private DataBaseMaintenance getDataBaseMaintenance(final RecreateIndexInput input) {
        DataSourceProperties dataSource = dataSourceConfigFactory.get(input.dataSourceName());
        return dataBaseMaintenanceFactory.getMaintenanceByType(dataSource.getProperties().dbType());
    }
}
