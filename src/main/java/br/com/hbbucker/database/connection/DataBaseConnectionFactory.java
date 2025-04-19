package br.com.hbbucker.database.connection;

import br.com.hbbucker.shared.database.DataBaseType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DataBaseConnectionFactory {

    private final DataBaseConnectionList dataBaseConnectionList;

    public DataBaseConnection get(DataBaseType dbType) {
        return dataBaseConnectionList.get(dbType);
    }
}
