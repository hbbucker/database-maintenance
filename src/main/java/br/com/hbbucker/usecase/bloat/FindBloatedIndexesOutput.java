package br.com.hbbucker.usecase.bloat;

import br.com.hbbucker.shared.database.index.IndexInfo;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public record FindBloatedIndexesOutput(List<IndexInfo> indexInfos) {
}
