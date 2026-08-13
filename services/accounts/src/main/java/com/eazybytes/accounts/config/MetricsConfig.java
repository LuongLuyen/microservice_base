package com.eazybytes.accounts.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter accountsCreatedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("eazybank.accounts.created.total")
                .description("Total number of accounts created")
                .register(meterRegistry);
    }

    @Bean
    public Counter accountsFetchedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("eazybank.accounts.fetched.total")
                .description("Total number of account fetch requests")
                .register(meterRegistry);
    }
}
