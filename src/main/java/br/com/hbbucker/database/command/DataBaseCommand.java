package br.com.hbbucker.database.command;

import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexInfo;

import java.util.List;

public interface DataBaseCommand {
    IndexInfo fetchIndexInfo(DataSourceName dataSourceName, String sql);
    List<IndexInfo> fetchAllIndexes(DataSourceName dataSourceName, String sql);
    void executeDDLCommand(DataSourceName dataSourceName, String ddl) throws Exception;
}
