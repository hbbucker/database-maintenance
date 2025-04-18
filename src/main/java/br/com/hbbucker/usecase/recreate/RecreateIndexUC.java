package br.com.hbbucker.usecase.recreate;

import br.com.hbbucker.database.DataBaseMaintenance;
import br.com.hbbucker.database.DataBaseMaintenanceFactory;
import br.com.hbbucker.metrics.IndexMetrics;
import br.com.hbbucker.shared.database.DataBaseType;
import br.com.hbbucker.shared.database.index.IndexInfo;
import br.com.hbbucker.usecase.Usecase;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RecreateIndexUC implements Usecase<RecreateIndexInput, Void>, Processor {

    private final DataBaseMaintenanceFactory dataBaseMaintenanceFactory;
    private final IndexMetrics metrics;

    @Override
    public void process(Exchange exchange) throws Exception {
        IndexInfo idx = exchange.getIn().getBody(IndexInfo.class);
        try {
            execute(new RecreateIndexInput(idx, DataBaseType.POSTGRESQL));
        } catch (Exception ignored) {
            // do nothing
        }
    }

    @Override
    public Void execute(RecreateIndexInput input) {
        metrics.markAnalyzed();
        IndexInfo index = input.index();

        DataBaseMaintenance dataBaseMaintenance = dataBaseMaintenanceFactory.get(input.databaseType());

        try {
            IndexInfo newIndex = dataBaseMaintenance.createIndex(index);
            dataBaseMaintenance.dropIndex(index);
            dataBaseMaintenance.renameIndex(newIndex.indexName, index);

            metrics.markSuccess();
        } catch (Exception e) {
            metrics.markFailure();
            Log.error("Failed to rebuild index: {1}", index.indexName, e);
            throw new RuntimeException("Failed to recreate index " + index.indexName, e);
        }

        return null;
    }
}
