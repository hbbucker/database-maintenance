package br.com.hbbucker.shared.database.index;

import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.ddl.DDLDefinition;
import br.com.hbbucker.shared.database.table.SchemaName;
import br.com.hbbucker.shared.database.table.TableName;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class IndexInfo {
    private DataSourceName dataSource;
    private SchemaName schemaName;
    private IndexName indexName;
    private TableName tableName;
    private BloatRatio bloatRatio;
    private DDLDefinition ddl;

    public String refactorCreateIndex(String oldValue, String newValue) {
        return ddl.ddl().replace(oldValue, newValue);
    }
}