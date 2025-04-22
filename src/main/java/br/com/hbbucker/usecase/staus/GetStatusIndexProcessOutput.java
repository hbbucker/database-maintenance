package br.com.hbbucker.usecase.staus;

import br.com.hbbucker.shared.cache.ProcessStatus;
import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexName;
import br.com.hbbucker.shared.database.table.TableName;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@RegisterForReflection
public final class GetStatusIndexProcessOutput {

    private List<IndexProcessing> indexProcessing = new ArrayList<>();

    public void add(final IndexProcessing indexProcessing) {
        if (this.indexProcessing == null) {
            this.indexProcessing = new java.util.ArrayList<>();
        }
        this.indexProcessing.add(indexProcessing);
    }

    @Builder
    @Getter
    public static class IndexProcessing {
        private DataSourceName dataSourceName;
        private TableName tableName;
        private IndexName indexName;
        @Setter
        private ProcessStatus status;
    }
}
