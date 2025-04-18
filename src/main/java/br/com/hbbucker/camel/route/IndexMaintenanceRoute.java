package br.com.hbbucker.camel.route;

import br.com.hbbucker.metrics.IndexMetrics;
import io.micrometer.core.instrument.Timer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import br.com.hbbucker.usecase.bloat.FindBloatedIndexesUC;
import br.com.hbbucker.usecase.recreate.RecreateIndexUC;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class IndexMaintenanceRoute extends RouteBuilder {

    private final IndexMetrics metrics;
    private final FindBloatedIndexesUC findBloatedIndexesUC;
    private final RecreateIndexUC recreateIndexUC;

    @Override
    public void configure() {
        from("direct:rebuild-indexes")
                .routeId("rebuild-indexes")
                .process(exchange -> exchange.setProperty("timerSample", metrics.startTimer()))
                .log("Starting index maintenance...")
                .process(findBloatedIndexesUC)
                .split(body())
                .parallelProcessing(false)
                .process(recreateIndexUC)
                .process(exchange -> {
                    Timer.Sample sample = exchange.getProperty("timerSample", Timer.Sample.class);
                    metrics.stopTimer(sample);
                })
                .log("Index maintenance completed.");
    }
}