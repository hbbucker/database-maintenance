package br.com.hbbucker.usecase.recreate;

import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexInfo;

public record RecreateIndexInput(IndexInfo index, DataSourceName dataSourceName) { }
