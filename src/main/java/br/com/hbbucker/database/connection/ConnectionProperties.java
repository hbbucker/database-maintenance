package br.com.hbbucker.database.connection;

import br.com.hbbucker.shared.database.DataBaseType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
@RegisterForReflection
public class ConnectionProperties {
    private DataBaseType dbType;
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;

    public DataBaseType dbType() {
        return dbType;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String database() {
        return database;
    }

    public String user() {
        return user;
    }

    public String password() {
        return password;
    }

}
