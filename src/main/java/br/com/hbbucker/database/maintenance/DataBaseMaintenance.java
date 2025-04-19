package br.com.hbbucker.database.maintenance;

import br.com.hbbucker.shared.database.DataBaseType;
import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexInfo;
import br.com.hbbucker.shared.database.index.IndexName;

import java.util.List;
import java.util.Optional;

public interface DataBaseMaintenance {
    List<IndexInfo> findBloatedIndexes(DataSourceName dataSourceName);
    Optional<IndexInfo> findIndexByName(DataSourceName dataSourceName, IndexInfo index);
    IndexInfo createIndex(DataSourceName dataSourceName, IndexInfo ddl);
    void dropIndex(DataSourceName dataSourceName, IndexInfo index);
    void renameIndex(DataSourceName dataSourceName, IndexName newName, IndexInfo index);
    DataBaseType getDataBaseType();
}
