package br.com.hbbucker.database.connection;

import br.com.hbbucker.shared.database.DataBaseType;

import java.sql.Connection;

public interface DataBaseConnection {

    /**
     * Creates a database connection using the provided properties.
     *
     * @param properties the connection properties
     * @return a valid database connection
     * @throws Exception if the connection cannot be established
     */
    Connection createConnection(ConnectionProperties properties) throws Exception;

    /**
     * Returns the type of database this connection supports.
     *
     * @return the database type
     */
    DataBaseType getDataBaseType();
}
