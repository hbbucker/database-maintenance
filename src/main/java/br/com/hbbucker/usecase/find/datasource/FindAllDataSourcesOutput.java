package br.com.hbbucker.usecase.find.datasource;

import br.com.hbbucker.shared.database.DataBaseType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@RegisterForReflection
public class FindAllDataSourcesOutput {
    @Getter
    private List<DSProperties> dataSources;

    public void addDataSource(DSProperties dsProperties) {
        if (dataSources == null) {
            dataSources = new ArrayList<>();
        }

        this.dataSources.add(dsProperties);
    }

    @Getter
    @Builder
    public static class DSProperties {
        private String dataSourceName;
        private DataBaseType dbType;
        private String host;
        private int port;
        private String database;
    }
}
