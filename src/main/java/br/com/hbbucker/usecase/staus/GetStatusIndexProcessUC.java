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
public class GetStatusIndexProcessUC implements Usecase<Void, GetStatusIndexProcessOutput> {

    private final CacheStatusService cacheStatusService;

    @Override
    public GetStatusIndexProcessOutput execute(Void input) {

        GetStatusIndexProcessOutput outputs = new GetStatusIndexProcessOutput();
        Set<String> keys = cacheStatusService.getKeys();

        keys.forEach(key -> {
            String[] parts = key.split(":");

            GetStatusIndexProcessOutput.IndexProcessing
                    processing = translate(parts);

            ProcessStatus status = cacheStatusService.get(
                    processing.dataSourceName,
                    processing.tableName,
                    processing.indexName);

            processing.setStatus(status);

            outputs.add(processing);
        });

        return outputs;
    }

    private GetStatusIndexProcessOutput.IndexProcessing translate(String[] parts) {
        return GetStatusIndexProcessOutput.IndexProcessing.builder()
                .dataSourceName(new DataSourceName(parts[2]))
                .tableName(new TableName(parts[3]))
                .indexName(new IndexName(parts[4]))
                .build();
    }
}
