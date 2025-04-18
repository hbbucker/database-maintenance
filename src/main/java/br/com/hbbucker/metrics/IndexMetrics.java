package br.com.hbbucker.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class IndexMetrics {

    @Inject
    MeterRegistry registry;

    private Counter indexesRebuilt;
    private Counter indexesFailed;
    private Counter totalAnalyzed;
    private Timer executionTimer;

    @PostConstruct
    void init() {
        indexesRebuilt = Counter.builder("index_rebuilder_rebuilt_total")
                .description("Total of indexes successfully rebuilt")
                .register(registry);

        indexesFailed = Counter.builder("index_rebuilder_failed_total")
                .description("Total of indexes that failed to rebuild")
                .register(registry);

        totalAnalyzed = Counter.builder("index_rebuilder_analyzed_total")
                .description("Total of indexes analyzed")
                .register(registry);

        executionTimer = Timer.builder("index_rebuilder_execution_duration")
                .description("Execution duration of index maintenance")
                .register(registry);
    }

    public void markSuccess() {
        indexesRebuilt.increment();
    }

    public void markFailure() {
        indexesFailed.increment();
    }

    public void markAnalyzed() {
        totalAnalyzed.increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start(registry);
    }

    public void stopTimer(Timer.Sample sample) {
        sample.stop(executionTimer);
    }
}