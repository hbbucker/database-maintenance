package br.com.hbbucker.usecase.bloat;

import br.com.hbbucker.shared.database.DataSourceName;

public record FindBloatedIndexesInput(DataSourceName dataSourceName) {
}