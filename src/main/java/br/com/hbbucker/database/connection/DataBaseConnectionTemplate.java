package br.com.hbbucker.database.connection;

import br.com.hbbucker.shared.database.DataBaseType;
import lombok.Getter;

@Getter
public enum DataBaseConnectionTemplate {
    POSTGRESQL(DataBaseType.POSTGRESQL,"org.postgresql.Driver", "jdbc:postgresql://%s:%s/%s?user=%s&password=%s");

    private final DataBaseType dbType;
    private final String driver;
    private final String url;

    DataBaseConnectionTemplate(DataBaseType dbType, String driver, String url) {
        this.dbType = dbType;
        this.driver = driver;
        this.url = url;
    }
}
