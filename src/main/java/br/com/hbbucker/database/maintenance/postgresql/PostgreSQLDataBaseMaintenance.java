package br.com.hbbucker.database.maintenance.postgresql;

import br.com.hbbucker.database.command.DataBaseCommand;
import br.com.hbbucker.database.maintenance.DataBaseMaintenance;
import br.com.hbbucker.shared.database.DataBaseType;
import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.ddl.DDLDefinition;
import br.com.hbbucker.shared.database.index.IndexInfo;
import br.com.hbbucker.shared.database.index.IndexName;
import br.com.hbbucker.shared.database.table.SchemaName;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public final class PostgreSQLDataBaseMaintenance implements DataBaseMaintenance {
    private final DataBaseCommand dataBaseCommand;

    private void logAndThrow(final String message, final Exception e) {
        Log.error(message, e);
        throw new IllegalStateException(message, e);
    }

    @Override
    public List<IndexInfo> findBloatedIndexes(final DataSourceName dataSourceName) {
        return dataBaseCommand.fetchAllIndexes(
                dataSourceName,
                PostgreSQLHelper.SQL_BLOATED_INDEX);
    }

    @Override
    public Optional<IndexInfo> findIndexByName(
            final DataSourceName dataSourceName,
            final SchemaName schemaName,
            final IndexName indexName) {
        return Optional.of(dataBaseCommand.fetchIndexInfo(
                dataSourceName,
                PostgreSQLHelper.SQL_GET_INDEX_INFO
                        .formatted(schemaName.name(), indexName.name())
        ));
    }

    @Override
    public IndexInfo createNewIndex(
            final DataSourceName dataSourceName,
            final IndexInfo indexInfo) {
        String newName = "%s_new".formatted(indexInfo.getIndexName().name());
        String newDDL = PostgreSQLHelper.REINDEX_CONCURRENTLY.formatted(indexInfo.getSchemaName().name(), indexInfo.getIndexName().name());
        try {
            Log.infof("Recreating index: %s", newDDL);

            dataBaseCommand.executeDDLCommand(dataSourceName, newDDL);

            return IndexInfo.builder()
                    .schemaName(indexInfo.getSchemaName())
                    .tableName(indexInfo.getTableName())
                    .indexName(new IndexName(newName))
                    .bloatRatio(indexInfo.getBloatRatio())
                    .ddl(new DDLDefinition(newDDL))
                    .build();

        } catch (Exception e) {
            logAndThrow("Failed to recreate index: " + indexInfo.getIndexName(), e);
            return null;
        }
    }

    @Override
    public void deleteIndex(
            final DataSourceName dataSourceName,
            final IndexInfo indexInfo) {

        //do nothing
    }

    @Override
    public void updateIndexName(
            final DataSourceName dataSourceName,
            final IndexName newName,
            final IndexInfo indexInfo) {
        //do nothing
    }

    @Override
    public DataBaseType getSupportedDataBaseType() {
        return DataBaseType.POSTGRESQL;
    }
}
