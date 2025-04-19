package br.com.hbbucker.database.connection;

import br.com.hbbucker.shared.database.DataBaseType;

import java.sql.Connection;

public interface DataBaseConnection {
    Connection createConnection(ConnectionProperties properties) throws Exception;
    DataBaseType getDataBaseType();
}
