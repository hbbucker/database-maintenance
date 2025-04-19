package br.com.hbbucker.database.connection;

import br.com.hbbucker.shared.database.DataBaseType;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Connection;
import java.sql.DriverManager;

@ApplicationScoped
public class PostgreSQLDatabaseConnection implements DataBaseConnection {

    public static final String JDBC_STRING = "jdbc:postgresql://%s:%s/%s";

    @Override
    public Connection createConnection(ConnectionProperties properties) throws Exception {
        String jdbcUrl = JDBC_STRING.formatted(properties.host(), properties.port(), properties.database());
        return DriverManager.getConnection(jdbcUrl, properties.user(), properties.password());
    }

    @Override
    public DataBaseType getDataBaseType() {
        return DataBaseType.POSTGRESQL;
    }
}
