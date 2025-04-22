package br.com.hbbucker.usecase.staus;

import br.com.hbbucker.service.CacheStatusService;
import br.com.hbbucker.shared.cache.ProcessStatus;
import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexName;
import br.com.hbbucker.shared.database.table.TableName;
import br.com.hbbucker.usecase.Usecase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public final class GetStatusIndexProcessUC implements Usecase<Void, GetStatusIndexProcessOutput> {

    public static final int ARRAY_POSITION_DATA_SOURCE = 2;
    public static final int ARRAY_POSITION_TABLE_NAME = 3;
    public static final int ARRAY_POSITION_INDEX_NAME = 4;
    public static final String PARTS_STRING_DELIMITER = ":";
    private final CacheStatusService cacheStatusService;

    @Override
    public GetStatusIndexProcessOutput execute(final Void input) {
        Set<String> keys = cacheStatusService.getKeys();
        return buildOutput(keys);
    }

    private GetStatusIndexProcessOutput buildOutput(final Set<String> keys) {
        GetStatusIndexProcessOutput outputs = new GetStatusIndexProcessOutput();
        keys.forEach(key -> processKey(key, outputs));
        return outputs;
    }

    private void processKey(final String key, final GetStatusIndexProcessOutput outputs) {
        String[] parts = key.split(PARTS_STRING_DELIMITER);
        GetStatusIndexProcessOutput.IndexProcessing processing = translate(parts);
        ProcessStatus status = fetchProcessStatus(processing);
        processing.setStatus(status);
        outputs.add(processing);
    }

    private ProcessStatus fetchProcessStatus(final GetStatusIndexProcessOutput.IndexProcessing processing) {
        return cacheStatusService.get(
                processing.getDataSourceName(),
                processing.getTableName(),
                processing.getIndexName()
        );
    }

    private GetStatusIndexProcessOutput.IndexProcessing translate(final String[] parts) {
        return GetStatusIndexProcessOutput.IndexProcessing.builder()
                .dataSourceName(new DataSourceName(parts[ARRAY_POSITION_DATA_SOURCE]))
                .tableName(new TableName(parts[ARRAY_POSITION_TABLE_NAME]))
                .indexName(new IndexName(parts[ARRAY_POSITION_INDEX_NAME]))
                .build();
    }
}
