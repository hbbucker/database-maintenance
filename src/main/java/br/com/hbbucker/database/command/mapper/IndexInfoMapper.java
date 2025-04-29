package br.com.hbbucker.database.command.mapper;

import br.com.hbbucker.shared.database.DataSourceName;
import br.com.hbbucker.shared.database.ddl.DDLDefinition;
import br.com.hbbucker.shared.database.index.BloatRatio;
import br.com.hbbucker.shared.database.index.IndexInfo;
import br.com.hbbucker.shared.database.index.IndexName;
import br.com.hbbucker.shared.database.index.IndexSize;
import br.com.hbbucker.shared.database.index.LastTimeIndexUsed;
import br.com.hbbucker.shared.database.index.TableSize;
import br.com.hbbucker.shared.database.index.TotalIndexScan;
import br.com.hbbucker.shared.database.index.TotalIndexTuplesFetched;
import br.com.hbbucker.shared.database.index.TotalIndexTuplesRead;
import br.com.hbbucker.shared.database.table.SchemaName;
import br.com.hbbucker.shared.database.table.TableName;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.IntStream;

@ApplicationScoped
public final class IndexInfoMapper implements ResultSetMapper<IndexInfo> {

    @Override
    public IndexInfo map(final ResultSet rs, final Object context) throws SQLException {
        DataSourceName dataSourceName = (DataSourceName) context;
        ResultSetMetaData metaData = rs.getMetaData();

        return IndexInfo.builder()
                .dataSource(dataSourceName)
                .schemaName(hasColumn(metaData, "schema_name")
                        ? new SchemaName(rs.getString("schema_name")) : null)
                .indexName(hasColumn(metaData, "index_name")
                        ? new IndexName(rs.getString("index_name")) : null)
                .tableName(hasColumn(metaData, "table_name")
                        ? new TableName(rs.getString("table_name")) : null)
                .bloatRatio(hasColumn(metaData, "bloat_ratio")
                        ? new BloatRatio(rs.getDouble("bloat_ratio")) : null)
                .totatIndexScan(hasColumn(metaData, "idx_scan")
                        ? new TotalIndexScan(rs.getLong("idx_scan")) : null)
                .lastTimeIndexUsed(hasColumn(metaData, "last_idx_scan")
                        ? new LastTimeIndexUsed(timestampToLocalDateTime(rs.getTimestamp("last_idx_scan"))) : null)
                .totalIndexTuplesFetched(hasColumn(metaData, "idx_tup_fetch")
                        ? new TotalIndexTuplesFetched(rs.getLong("idx_tup_fetch")) : null)
                .totalIndexTuplesRead(hasColumn(metaData, "idx_tup_read")
                        ? new TotalIndexTuplesRead(rs.getLong("idx_tup_read")) : null)
                .indexSize(hasColumn(metaData, "idx_size")
                        ? new IndexSize(rs.getLong("idx_size")) : null)
                .tableSize(hasColumn(metaData, "tbl_size")
                        ? new TableSize(rs.getLong("tbl_size")) : null)
                .ddl(hasColumn(metaData, "ddl")
                        ? new DDLDefinition(rs.getString("ddl")) : null)
                .build();
    }

    private boolean hasColumn(final ResultSetMetaData metaData, final String columnName) throws SQLException {
        return IntStream.rangeClosed(1, metaData.getColumnCount())
                .anyMatch(i -> hasColumn(metaData, columnName, i));
    }

    private boolean hasColumn(final ResultSetMetaData metaData, final String columnName, int i) {
        try {
            return metaData.getColumnName(i).equalsIgnoreCase(columnName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static LocalDateTime timestampToLocalDateTime(final Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
