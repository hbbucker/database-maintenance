package br.com.hbbucker.usecase.bloat;

import br.com.hbbucker.shared.database.DataSourceName;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record FindBloatedIndexesInput(DataSourceName dataSourceName) {
}