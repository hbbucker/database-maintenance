package br.com.hbbucker.database.postgresql;

import br.com.hbbucker.shared.database.DataBaseType;
import br.com.hbbucker.shared.database.ddl.DDLDefinition;
import br.com.hbbucker.database.DataBaseMaintenance;
import br.com.hbbucker.shared.database.index.*;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PostgreSQLDataBaseMaintenance implements DataBaseMaintenance {
    private static final String INDEX_CONCURRENTLY = "INDEX CONCURRENTLY %s";
    private static final String DROP_INDEX = "DROP INDEX CONCURRENTLY IF EXISTS %s";
    private static final String ALTER_INDEX = "ALTER INDEX %s RENAME TO %s";
    private static final String INDEX = "INDEX %s";

    private final DataSource dataSource;

    @Override
    public List<IndexInfo> findBloatedIndexes() {
        List<IndexInfo> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(PostgreSQLHelper.SQL_BLOATED_INDEX);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                IndexInfo idx = IndexInfo.builder()
                        .schema(new SchemaName(rs.getString("schema")))
                        .indexName(new IndexName(rs.getString("index_name")))
                        .tableName(new TableName(rs.getString("table_name")))
                        .bloatRatio(new BloatRatio(rs.getDouble("bloat_ratio")))
                        .ddl(new DDLDefinition(rs.getString("ddl")))
                        .build();
                result.add(idx);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching bloated indexes", e);
        }

        return result;
    }

    /**
     * Recreate the index with the new name
     *
     * @param index the index to be recreated
     * @return the new index info
     */
    @Override
    public IndexInfo createIndex(IndexInfo index) {
        String newName = "%s_new".formatted(index.indexName.name());
        String newDDL = index.refactorDll(INDEX.formatted(index.indexName.name()), INDEX_CONCURRENTLY.formatted(newName));
        try {
            Log.infof("Recreating index: %s", newDDL);
            execute(newDDL);
            return IndexInfo.builder()
                    .schema(index.schema)
                    .indexName(new IndexName(newName))
                    .tableName(index.tableName)
                    .bloatRatio(index.bloatRatio)
                    .ddl(new DDLDefinition(newDDL))
                    .build();
        } catch (Exception e) {
            Log.error("Failed to rebuild index: {1}", index.indexName, e);
            throw new RuntimeException("Failed to recreate index " + index.indexName, e);
        }
    }

    /**
     * Drop the old index
     *
     * @param index the index to be dropped
     */
    @Override
    public void dropIndex(IndexInfo index) {
        try {
            Log.infof("Droping old index: %s.%s", index.schema.name(), index.indexName.name());
            execute(DROP_INDEX.formatted(index.schema.name() + "." + index.indexName.name()));
        } catch (Exception e) {
            Log.error("Failed to drop index: {1}", index.indexName.name(), e);
            throw new RuntimeException("Failed to drop index " + index.indexName.name(), e);
        }
    }

    /**
     * Rename the index to the old name
     *
     * @param newName
     * @param index
     */
    @Override
    public void renameIndex(IndexName newName, IndexInfo index) {
        try {
            Log.infof("Renaming index: %s -> %s.%s", newName.name(), index.schema.name(), index.indexName.name());
            execute(ALTER_INDEX.formatted(index.schema.name() + "." + newName.name(), index.indexName.name()));
        } catch (Exception e) {
            Log.error("Failed to rename index: {1}", index.indexName.name(), e);
            throw new RuntimeException("Failed to rename index " + index.indexName.name(), e);
        }
    }

    @Override
    public DataBaseType getDataBaseType() {
        return DataBaseType.POSTGRESQL;
    }

    private void execute(String sql) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(true);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        } catch (Exception e) {
            throw new RuntimeException("[Failed] " + sql, e);
        }


    }
}
