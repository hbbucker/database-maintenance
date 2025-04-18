package br.com.hbbucker.usecase.bloat;

import br.com.hbbucker.shared.database.DataBaseType;

public record FindBloatedIndexesInput(DataBaseType dataBaseType) {
}
