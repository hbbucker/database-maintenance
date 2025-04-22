package br.com.hbbucker.database.connection;

import br.com.hbbucker.shared.database.DataBaseType;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Connection;
import java.sql.DriverManager;

@ApplicationScoped
public final class PostgreSQLDatabaseConnection implements DataBaseConnection {

    private static final String JDBC_TEMPLATE = "jdbc:postgresql://%s:%s/%s";

    @Override
    public Connection createConnection(final ConnectionProperties properties) throws Exception {
        String jdbcUrl = JDBC_TEMPLATE.formatted(properties.host(), properties.port(), properties.database());
        return DriverManager.getConnection(jdbcUrl, properties.user(), properties.password());
    }

    @Override
    public DataBaseType getDataBaseType() {
        return DataBaseType.POSTGRESQL;
    }
}
