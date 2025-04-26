package br.com.hbbucker.database.command;

import br.com.hbbucker.database.DataSourceProperties;
import br.com.hbbucker.database.command.mapper.ResultSetMapper;
import br.com.hbbucker.database.command.validation.SQLValidator;
import br.com.hbbucker.database.config.DataSourceConfigFactory;
import br.com.hbbucker.database.connection.DataBaseConnectionFactory;
import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.index.IndexInfo;
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
    private final SQLValidator sqlValidator;
    private final ResultSetMapper<IndexInfo> indexInfoMapper;

    @Override
    public IndexInfo fetchIndexInfo(final DataSourceName dataSourceName, final String sql) {
        sqlValidator.validateQuery(sql);
        DataSourceProperties dataSource = configFactory.get(dataSourceName);

        try (Connection conn = createConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return indexInfoMapper.map(rs, dataSourceName);
            }
            throw new IllegalStateException("Index not found for query: " + sql);
        } catch (Exception ex) {
            Log.errorf("Error fetching index info: %s", sql, ex);
            throw new RuntimeException("Error fetching index info", ex);
        }
    }

    @Override
    public List<IndexInfo> fetchAllIndexes(final DataSourceName dataSourceName, final String sql) {
        sqlValidator.validateQuery(sql);
        DataSourceProperties dataSource = configFactory.get(dataSourceName);
        List<IndexInfo> indexes = new ArrayList<>();

        try (Connection conn = createConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                indexes.add(indexInfoMapper.map(rs, dataSourceName));
            }
        } catch (Exception ex) {
            Log.errorf(ex, "Error fetching all indexes: %s", sql);
            throw new RuntimeException("Error fetching all indexes", ex);
        }
        return indexes;
    }

    @Override
    public void executeDDLCommand(final DataSourceName dataSourceName, final String ddl) throws Exception {
        sqlValidator.validateDDL(ddl);
        DataSourceProperties dataSource = configFactory.get(dataSourceName);

        try (Connection conn = createConnection(dataSource);
             var statement = conn.createStatement()) {
            conn.setAutoCommit(true);
            statement.execute(ddl);
        } catch (SQLException ex) {
            Log.errorf(ex, "Error executing DDL: %s", ddl);
            throw new RuntimeException("Error executing DDL", ex);
        }
    }

    private Connection createConnection(final DataSourceProperties dataSource) throws Exception {
        return connectionFactory.getConnectionByType(dataSource.getProperties().dbType())
                .createConnection(dataSource.getProperties());
    }
}
