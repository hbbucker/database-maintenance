package br.com.hbbucker.database;

import br.com.hbbucker.shared.database.DataBaseType;
import br.com.hbbucker.shared.database.index.IndexInfo;
import br.com.hbbucker.shared.database.index.IndexName;

import java.util.List;

public interface DataBaseMaintenance {
    List<IndexInfo> findBloatedIndexes();
    IndexInfo createIndex(IndexInfo ddl);
    void dropIndex(IndexInfo index);
    void renameIndex(IndexName newName, IndexInfo index);
    DataBaseType getDataBaseType();
}
