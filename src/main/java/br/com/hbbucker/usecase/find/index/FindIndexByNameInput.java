package br.com.hbbucker.usecase.find.index;

import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexName;
import br.com.hbbucker.shared.database.table.SchemaName;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record FindIndexByNameInput(DataSourceName dataSourceName,
                                   SchemaName schemaName,
                                   IndexName indexName) {
}
