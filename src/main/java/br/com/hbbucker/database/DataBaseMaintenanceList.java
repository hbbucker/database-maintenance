package br.com.hbbucker.database;

import br.com.hbbucker.shared.database.DataBaseType;
import io.quarkus.arc.All;
import io.quarkus.arc.InstanceHandle;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
//@RequiredArgsConstructor(onConstructor=@__({@Inject, @All}))
class DataBaseMaintenanceList {
    @All
    @Inject
    protected List<InstanceHandle<DataBaseMaintenance>> list;

    protected DataBaseMaintenance get(DataBaseType dataBaseType) {
        return list.stream()
                .filter(instanceHandle -> dataBaseType == instanceHandle.get().getDataBaseType())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No DataBaseMaintenance found for type: " + dataBaseType))
                .get();
    }

}
