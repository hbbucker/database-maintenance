package br.com.hbbucker.database.command;

import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexInfo;

import java.util.List;

public interface DataBaseCommand {
    IndexInfo getIndexInfo(final DataSourceName dataSourceName, final String sql);
    List<IndexInfo> getAllIndex(final DataSourceName dataSourceName, final String sql);
    void executeDDL(final DataSourceName dataSourceName, final String ddl) throws Exception;
}
