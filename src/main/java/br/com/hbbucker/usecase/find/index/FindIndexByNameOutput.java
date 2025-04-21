package br.com.hbbucker.usecase.find.index;

import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexInfo;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record FindIndexByNameOutput(DataSourceName dataSourceName,
                                    IndexInfo indexInfo) {
}
