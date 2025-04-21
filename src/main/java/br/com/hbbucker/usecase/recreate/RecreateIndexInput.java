package br.com.hbbucker.usecase.recreate;

import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexInfo;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record RecreateIndexInput(IndexInfo index, DataSourceName dataSourceName) { }
