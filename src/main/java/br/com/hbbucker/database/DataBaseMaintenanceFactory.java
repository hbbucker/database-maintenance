package br.com.hbbucker.database;

import br.com.hbbucker.shared.database.DataBaseType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DataBaseMaintenanceFactory {
    private final DataBaseMaintenanceList dataBaseMaintenanceList;

    public DataBaseMaintenance get(DataBaseType dataBaseType) {
        return dataBaseMaintenanceList.get(dataBaseType);
    }
}
