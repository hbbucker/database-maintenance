package br.com.hbbucker.database.maintenance.postgresql;

import br.com.hbbucker.database.command.DataBaseCommand;
import br.com.hbbucker.database.maintenance.DataBaseMaintenance;
import br.com.hbbucker.shared.database.DataBaseType;
import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.ddl.DDLDefinition;
import br.com.hbbucker.shared.database.index.IndexInfo;
import br.com.hbbucker.shared.database.index.IndexName;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PostgreSQLDataBaseMaintenance implements DataBaseMaintenance {
    private final DataBaseCommand dataBaseCommand;

    @Override
    public List<IndexInfo> findBloatedIndexes(DataSourceName dataSourceName) {
        return dataBaseCommand.getAllIndex(
                dataSourceName,
                PostgreSQLHelper.SQL_BLOATED_INDEX);
    }

    @Override
    public Optional<IndexInfo> findIndexByName(DataSourceName dataSourceName, IndexInfo index) {
        return Optional.of(dataBaseCommand.getIndexInfo(
                dataSourceName,
                PostgreSQLHelper.SQL_INDEX_INFO
                        .formatted(index.getSchemaName().name(), index.getIndexName().name())
        ));
    }

    /**
     * Recreate the index with the new name
     *
     * @param index the index to be recreated
     * @return the new index info
     */
    @Override
    public IndexInfo createIndex(DataSourceName dataSourceName, IndexInfo index) {
        String newName = "%s_new".formatted(index.getIndexName().name());
        String newDDL = index.refactorCreateIndex(
                PostgreSQLHelper.INDEX.formatted(index.getIndexName().name()),
                PostgreSQLHelper.INDEX_CONCURRENTLY.formatted(newName));
        try {
            Log.infof("Recreating index: %s", newDDL);

            dataBaseCommand.executeDDL(dataSourceName, newDDL);

            return IndexInfo.builder()
                    .schemaName(index.getSchemaName())
                    .tableName(index.getTableName())
                    .indexName(new IndexName(newName))
                    .bloatRatio(index.getBloatRatio())
                    .ddl(new DDLDefinition(newDDL))
                    .build();

        } catch (Exception e) {
            Log.error("Failed to rebuild index: {1}", index.getIndexName(), e);
            throw new RuntimeException("Failed to recreate index " + index.getIndexName(), e);
        }
    }

    /**
     * Drop the old index
     *
     * @param index the index to be dropped
     */
    @Override
    public void dropIndex(DataSourceName dataSourceName, IndexInfo index) {
        try {
            Log.infof("Droping old index: %s.%s",
                    index.getSchemaName().name(),
                    index.getIndexName().name());

            dataBaseCommand.executeDDL(
                    dataSourceName,
                    PostgreSQLHelper.DROP_INDEX
                            .formatted(index.getSchemaName().name(), index.getIndexName().name()));
        } catch (Exception e) {
            Log.error("Failed to drop index: {1}", index.getIndexName().name(), e);
            throw new RuntimeException("Failed to drop index " + index.getIndexName().name(), e);
        }
    }

    /**
     * Rename the index to the old name
     *
     * @param newName
     * @param index
     */
    @Override
    public void renameIndex(DataSourceName dataSourceName, IndexName newName, IndexInfo index) {
        try {
            Log.infof("Renaming index: %s -> %s.%s",
                    newName.name(),
                    index.getSchemaName().name(),
                    index.getIndexName().name());

            dataBaseCommand.executeDDL(
                    dataSourceName,
                    PostgreSQLHelper.ALTER_INDEX
                            .formatted(index.getSchemaName().name(), newName.name(), index.getIndexName().name()));
        } catch (Exception e) {
            Log.error("Failed to rename index: {1}", index.getIndexName().name(), e);
            throw new RuntimeException("Failed to rename index " + index.getIndexName().name(), e);
        }
    }

    @Override
    public DataBaseType getDataBaseType() {
        return DataBaseType.POSTGRESQL;
    }
}
