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
    private List<InstanceHandle<DataBaseConnection>> connections;

    /**
     * Get the DataBaseConnection implementation for the specified DataBaseType.
     * @param dataBaseType the DataBaseType to get the connection for
     * @return the DataBaseConnection implementation
     */
    protected DataBaseConnection get(final DataBaseType dataBaseType) {
        return connections.stream()
                .filter(instance -> dataBaseType == instance.get().getDataBaseType())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No DataBaseConnection implementation found for type: " + dataBaseType))
                .get();
    }
}
