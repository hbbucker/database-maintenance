package br.com.hbbucker.usecase.bloat;

import br.com.hbbucker.shared.database.index.IndexInfo;

import java.util.List;

public record FindBloatedIndexesOutput (List<IndexInfo> indexInfos) { }
