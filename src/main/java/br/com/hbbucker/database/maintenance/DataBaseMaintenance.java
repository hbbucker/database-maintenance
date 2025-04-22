package br.com.hbbucker.database.maintenance;

import br.com.hbbucker.shared.database.DataBaseType;
import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexInfo;
import br.com.hbbucker.shared.database.index.IndexName;
import br.com.hbbucker.shared.database.table.SchemaName;

import java.util.List;
import java.util.Optional;

public interface DataBaseMaintenance {
    List<IndexInfo> findBloatedIndexes(DataSourceName dataSourceName);
    Optional<IndexInfo> findIndexByName(DataSourceName dataSourceName, SchemaName schemaName, IndexName indexName);
    IndexInfo createNewIndex(DataSourceName dataSourceName, IndexInfo indexInfo);
    void deleteIndex(DataSourceName dataSourceName, IndexInfo indexInfo);
    void updateIndexName(DataSourceName dataSourceName, IndexName newName, IndexInfo indexInfo);
    DataBaseType getSupportedDataBaseType();
}
