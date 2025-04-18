package br.com.hbbucker.usecase.bloat;

import br.com.hbbucker.database.DataBaseMaintenance;
import br.com.hbbucker.database.DataBaseMaintenanceFactory;
import br.com.hbbucker.shared.database.index.IndexInfo;
import br.com.hbbucker.usecase.Usecase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.sql.DataSource;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class FindBloatedIndexesUC implements Usecase<FindBloatedIndexesInput, FindBloatedIndexesOutput>, Processor {
    private final DataSource dataSource;
    private final DataBaseMaintenanceFactory dataBaseMaintenanceFactory;

    @Override
    public void process(Exchange exchange) throws Exception {
        FindBloatedIndexesInput input = exchange.getMessage().getBody(FindBloatedIndexesInput.class);
        FindBloatedIndexesOutput output = execute(input);
        exchange.getMessage().setBody(output.indexInfos());
    }

    @Override
    public FindBloatedIndexesOutput execute(FindBloatedIndexesInput indexesInput) {
        DataBaseMaintenance dataBaseMaintenance = dataBaseMaintenanceFactory.get(indexesInput.dataBaseType());
        List<IndexInfo> result = dataBaseMaintenance.findBloatedIndexes();

        return new FindBloatedIndexesOutput(result);
    }

}
