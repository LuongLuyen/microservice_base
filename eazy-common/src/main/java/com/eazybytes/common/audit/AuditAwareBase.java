package com.eazybytes.common.audit;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public abstract class AuditAwareBase implements AuditorAware<String> {

    protected abstract String getServiceName();

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(getServiceName());
    }
}
