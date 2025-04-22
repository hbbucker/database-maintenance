package br.com.hbbucker.database.connection;

import br.com.hbbucker.shared.database.DataBaseType;
import lombok.Getter;

@Getter
public enum DataBaseConnectionTemplate {
    POSTGRESQL(DataBaseType.POSTGRESQL, "org.postgresql.Driver", "jdbc:postgresql://%s:%s/%s?user=%s&password=%s");

    private final DataBaseType dbType;
    private final String driver;
    private final String url;

    DataBaseConnectionTemplate(final DataBaseType dbType, final String driver, final String url) {
        validateUrl(url);
        this.dbType = dbType;
        this.driver = driver;
        this.url = url;
    }

    private void validateUrl(final String url) {
        if (!url.contains("jdbc:")) {
            throw new IllegalArgumentException("Invalid JDBC URL format: " + url);
        }
    }
}
