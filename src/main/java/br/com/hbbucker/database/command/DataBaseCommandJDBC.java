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
public class DataBaseCommandJDBC implements DataBaseCommand {

    private final DataBaseConnectionFactory dataBaseConnectionFactory;
    private final DataSourceConfigFactory dataSourceConfigFactory;

    public IndexInfo getIndexInfo(final DataSourceName dataSourceName, final String sql) {

        SQLInjectionMonitor.monitorSQLInjection(sql);

        DataSourceProperties dataSource = dataSourceConfigFactory.get(dataSourceName);

        try (Connection conn = dataBaseConnectionFactory.get(dataSource.getProperties().dbType()).createConnection(dataSource.getProperties());
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return translateResultSet(rs, dataSourceName);
            }
            throw new RuntimeException("Index not found: " + sql);
        } catch (Exception ex) {
            throw new RuntimeException("Error find index: " + sql, ex);
        }
    }

    private IndexInfo translateResultSet(final ResultSet rs, final DataSourceName dataSourceName) throws SQLException {
        return IndexInfo.builder()
                .dataSource(dataSourceName)
                .schemaName(new SchemaName(rs.getString("schema")))
                .indexName(new IndexName(rs.getString("index_name")))
                .tableName(new TableName(rs.getString("table_name")))
                .bloatRatio(new BloatRatio(rs.getDouble("bloat_ratio")))
                .ddl(new DDLDefinition(rs.getString("ddl")))
                .build();

    }

    public List<IndexInfo> getAllIndex(final DataSourceName dataSourceName, final String sql) {

        SQLInjectionMonitor.monitorSQLInjection(sql);

        DataSourceProperties dataSource = dataSourceConfigFactory.get(dataSourceName);
        List<IndexInfo> result = new ArrayList<>();

        try (Connection conn = dataBaseConnectionFactory.get(dataSource.getProperties().dbType()).createConnection(dataSource.getProperties());
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(translateResultSet(rs, dataSourceName));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error executing SQL: " + sql, ex);
        }
        return result;
    }

    public void executeDDL(final DataSourceName dataSourceName, final String ddl) throws Exception {

        SQLInjectionMonitor.monitorDDLInjection(ddl);

        DataSourceProperties dataSource = dataSourceConfigFactory.get(dataSourceName);

        try (Connection connection = dataBaseConnectionFactory.get(dataSource.getProperties().dbType())
                .createConnection(dataSource.getProperties())) {

            connection.setAutoCommit(true);
            try (var statement = connection.createStatement()) {
                statement.execute(ddl);
            } catch (Exception e) {
                throw e;
            }
        }
    }
}
