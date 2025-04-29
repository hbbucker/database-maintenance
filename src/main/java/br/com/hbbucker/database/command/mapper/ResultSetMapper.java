package br.com.hbbucker.database.command.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetMapper<T> {
    T map(ResultSet resultSet, Object context) throws SQLException;
}
