package br.com.hbbucker.database;

import br.com.hbbucker.database.connection.ConnectionProperties;
import br.com.hbbucker.shared.database.DataBaseType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@RegisterForReflection
public class DataSourceProperties {
    @Setter
    private String sourceName;
    @Builder.Default
    private ConnectionProperties properties = ConnectionProperties.builder().build();

    public void setDbType(DataBaseType dataBaseType) {
        properties.setDbType(dataBaseType);
    }

    public void setHost(String value) {
        properties.setHost(value);
    }

    public void setPort(int i) {
        properties.setPort(i);
    }

    public void setUser(String value) {
        properties.setUser(value);
    }

    public void setPassword(String value) {
        properties.setPassword(value);
    }

    public void setDatabase(String value) {
        properties.setDatabase(value);
    }
}
