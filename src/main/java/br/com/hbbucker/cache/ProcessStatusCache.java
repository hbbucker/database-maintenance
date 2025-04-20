package br.com.hbbucker.cache;

import br.com.hbbucker.shared.cache.ProcessStatus;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Named
@Singleton
public class ProcessStatusCache extends CacheCaffeine<ProcessStatus> {

    @Inject
    public ProcessStatusCache(@CacheName("local-cache") Cache cache) {
        super(cache);
    }
}
