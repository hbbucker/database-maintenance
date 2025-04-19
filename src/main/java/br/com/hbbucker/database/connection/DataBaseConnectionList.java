package br.com.hbbucker.database.connection;


import br.com.hbbucker.shared.database.DataBaseType;
import io.quarkus.arc.All;
import io.quarkus.arc.InstanceHandle;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class DataBaseConnectionList {
    @All
    @Inject
    List<InstanceHandle<DataBaseConnection>> list;

    protected DataBaseConnection get(DataBaseType dataBaseType) {
        return list.stream()
                .filter(instanceHandle -> dataBaseType == instanceHandle.get().getDataBaseType())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No DataBaseConnection found for type: " + dataBaseType))
                .get();
    }
}
