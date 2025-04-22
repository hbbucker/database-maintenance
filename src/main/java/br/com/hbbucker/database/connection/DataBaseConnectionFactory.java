package br.com.hbbucker.database.connection;

import br.com.hbbucker.shared.database.DataBaseType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public final class DataBaseConnectionFactory {

    private final DataBaseConnectionList connectionList;

    public DataBaseConnection getConnectionByType(final DataBaseType dbType) {
        return connectionList.get(dbType);
    }
}
