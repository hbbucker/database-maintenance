package br.com.hbbucker.shared.database.index;

import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.ddl.DDLDefinition;
import br.com.hbbucker.shared.database.table.SchemaName;
import br.com.hbbucker.shared.database.table.TableName;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@RegisterForReflection
public final class IndexInfo {
    private DataSourceName dataSource;
    private SchemaName schemaName;
    private IndexName indexName;
    private TableName tableName;
    private BloatRatio bloatRatio;
    private DDLDefinition ddl;
    private TotalIndexScan totatIndexScan;
    private LastTimeIndexUsed lastTimeIndexUsed;
    private TotalIndexTuplesFetched totalIndexTuplesFetched;
    private TotalIndexTuplesRead totalIndexTuplesRead;
    private IndexSize indexSize;
    private TableSize tableSize;

    public String refactorCreateIndex(final String oldValue, final String newValue) {
        return ddl.ddl().replace(oldValue, newValue);
    }
}
