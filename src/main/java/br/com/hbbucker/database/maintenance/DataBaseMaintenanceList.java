package br.com.hbbucker.database.maintenance;

// ...existing imports...

import br.com.hbbucker.shared.database.DataBaseType;
import io.quarkus.arc.All;
import io.quarkus.arc.InstanceHandle;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
class DataBaseMaintenanceList {

    @All
    @Inject
    private List<InstanceHandle<DataBaseMaintenance>> maintenanceInstances;

    protected DataBaseMaintenance get(final DataBaseType dataBaseType) {
        return maintenanceInstances.stream()
                .filter(instance -> dataBaseType == instance.get().getSupportedDataBaseType())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No DataBaseMaintenance implementation found for type: " + dataBaseType))
                .get();
    }
}
