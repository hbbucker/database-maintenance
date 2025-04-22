package br.com.hbbucker.database.maintenance;

import br.com.hbbucker.shared.database.DataBaseType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public final class DataBaseMaintenanceFactory {

    private final DataBaseMaintenanceList maintenanceList;

    public DataBaseMaintenance getMaintenanceByType(final DataBaseType dataBaseType) {
        return maintenanceList.get(dataBaseType);
    }
}
