package br.com.hbbucker.camel.route;

import br.com.hbbucker.metrics.IndexMetrics;
import br.com.hbbucker.usecase.bloat.FindBloatedIndexesUC;
import br.com.hbbucker.usecase.find.index.FindIndexByNameUC;
import br.com.hbbucker.usecase.recreate.RecreateIndexUC;
import io.micrometer.core.instrument.Timer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class IndexMaintenanceRoute extends RouteBuilder {

    private final IndexMetrics metrics;
    private final FindBloatedIndexesUC findBloatedIndexesUC;
    private final FindIndexByNameUC findIndexByNameUC;
    private final RecreateIndexUC recreateIndexUC;

    @Override
    public void configure() {
        configureRebuildAllIndexRoute();
        configureRebuildIndexByNameRoute();
    }

    private void configureRebuildAllIndexRoute() {
        from("direct:rebuild-indexes")
                .routeId("rebuild-indexes")
                .process(this::startTimer)
                .log("Starting index maintenance...")
                .process(findBloatedIndexesUC)
                .split(body())
                .parallelProcessing(false)
                .process(recreateIndexUC)
                .process(this::stopTimer)
                .log("Index maintenance completed.");
    }

    private void configureRebuildIndexByNameRoute() {
        from("direct:rebuild-index")
                .routeId("rebuild-index")
                .process(this::startTimer)
                .log("Starting index maintenance...")
                .process(findIndexByNameUC)
                .split(body())
                .parallelProcessing(false)
                .process(recreateIndexUC)
                .process(this::stopTimer)
                .log("Index maintenance completed.");
    }

    private void startTimer(final Exchange exchange) {
        exchange.setProperty("timerSample", metrics.startTimer());
    }

    private void stopTimer(final Exchange exchange) {
        Timer.Sample sample = exchange.getProperty("timerSample", Timer.Sample.class);
        metrics.stopTimer(sample);
    }

}
