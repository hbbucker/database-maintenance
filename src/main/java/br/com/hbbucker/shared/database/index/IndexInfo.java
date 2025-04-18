package br.com.hbbucker.shared.database.index;

import br.com.hbbucker.shared.database.ddl.DDLDefinition;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IndexInfo {
    public SchemaName schema;
    public IndexName indexName;
    public TableName tableName;
    public BloatRatio bloatRatio;
    public DDLDefinition ddl;

    public String refactorDll(String oldValue, String newValue) {
        return ddl.ddl().replace(oldValue, newValue);
    }
}