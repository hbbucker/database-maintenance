package br.com.hbbucker.database.command;

import br.com.hbbucker.database.DataSourceProperties;
import br.com.hbbucker.database.config.DataSourceConfigFactory;
import br.com.hbbucker.database.connection.DataBaseConnectionFactory;
import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.ddl.DDLDefinition;
import br.com.hbbucker.shared.database.index.BloatRatio;
import br.com.hbbucker.shared.database.index.IndexInfo;
import br.com.hbbucker.shared.database.index.IndexName;
import br.com.hbbucker.shared.database.table.SchemaName;
import br.com.hbbucker.shared.database.table.TableName;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public final class DataBaseCommandJDBC implements DataBaseCommand {

    private final DataBaseConnectionFactory connectionFactory;
    private final DataSourceConfigFactory configFactory;

    @Override
    public IndexInfo fetchIndexInfo(final DataSourceName dataSourceName, final String sql) {
        validateSQL(sql);
        DataSourceProperties dataSource = configFactory.get(dataSourceName);

        try (Connection conn = createConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapToIndexInfo(rs, dataSourceName);
            }
            throw new IllegalStateException("Index not found for query: " + sql);
        } catch (SQLException ex) {
            Log.errorf("Error fetching index info: %s", sql, ex);
            throw new RuntimeException("Error fetching index info", ex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<IndexInfo> fetchAllIndexes(final DataSourceName dataSourceName, final String sql) {
        validateSQL(sql);
        DataSourceProperties dataSource = configFactory.get(dataSourceName);
        List<IndexInfo> indexes = new ArrayList<>();

        try (Connection conn = createConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                indexes.add(mapToIndexInfo(rs, dataSourceName));
            }
        } catch (SQLException ex) {
            Log.errorf("Error fetching all indexes: %s", sql, ex);
            throw new RuntimeException("Error fetching all indexes", ex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return indexes;
    }

    @Override
    public void executeDDLCommand(final DataSourceName dataSourceName, final String ddl) throws Exception {
        validateDDL(ddl);
        DataSourceProperties dataSource = configFactory.get(dataSourceName);

        try (Connection conn = createConnection(dataSource);
             var statement = conn.createStatement()) {
            conn.setAutoCommit(true);
            statement.execute(ddl);
        } catch (SQLException ex) {
            Log.errorf("Error executing DDL: %s", ddl, ex);
            throw new RuntimeException("Error executing DDL", ex);
        }
    }

    private Connection createConnection(final DataSourceProperties dataSource) throws Exception {
        return connectionFactory.getConnectionByType(dataSource.getProperties().dbType())
                .createConnection(dataSource.getProperties());
    }

    private IndexInfo mapToIndexInfo(
            final ResultSet rs,
            final DataSourceName dataSourceName) throws SQLException {
        return IndexInfo.builder()
                .dataSource(dataSourceName)
                .schemaName(new SchemaName(rs.getString("schema_name")))
                .indexName(new IndexName(rs.getString("index_name")))
                .tableName(new TableName(rs.getString("table_name")))
                .bloatRatio(new BloatRatio(rs.getDouble("bloat_ratio")))
                .ddl(new DDLDefinition(rs.getString("ddl")))
                .build();
    }

    private void validateSQL(final String sql) {
        SQLInjectionMonitor.validateSQLQuery(sql);
    }

    private void validateDDL(final String ddl) {
        SQLInjectionMonitor.validateDDLInjection(ddl);
    }
}
